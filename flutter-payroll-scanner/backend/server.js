/**
 * Backend Proxy Service for Payroll Scanner
 *
 * This Node.js/Express server securely proxies requests to the Gemini API
 * while keeping API keys safe and implementing authentication, rate limiting,
 * and caching.
 */

const express = require('express');
const { GoogleGenerativeAI } = require('@google/generative-ai');
const admin = require('firebase-admin');
const rateLimit = require('express-rate-limit');
const helmet = require('helmet');
const cors = require('cors');
require('dotenv').config();

// Initialize Express
const app = express();
const PORT = process.env.PORT || 3000;

// Security middleware
app.use(helmet());
app.use(cors({
  origin: process.env.ALLOWED_ORIGINS?.split(',') || '*',
  credentials: true,
}));
app.use(express.json({ limit: '10mb' }));

// Initialize Firebase Admin
admin.initializeApp({
  credential: admin.credential.applicationDefault(),
  projectId: process.env.FIREBASE_PROJECT_ID,
});

const db = admin.firestore();

// Initialize Gemini AI (server-side only - API key is secure)
const genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY);

// Rate limiting - 10 requests per minute per user
const limiter = rateLimit({
  windowMs: 60 * 1000, // 1 minute
  max: 10,
  message: 'Too many requests, please try again later.',
  standardHeaders: true,
  legacyHeaders: false,
  keyGenerator: (req) => req.user?.uid || req.ip,
});

// Middleware: Verify Firebase Authentication Token
async function verifyAuth(req, res, next) {
  const authHeader = req.headers.authorization;

  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return res.status(401).json({ error: 'Unauthorized - No token provided' });
  }

  const token = authHeader.split('Bearer ')[1];

  try {
    const decodedToken = await admin.auth().verifyIdToken(token);
    req.user = decodedToken;
    next();
  } catch (error) {
    console.error('Auth error:', error);
    return res.status(401).json({ error: 'Unauthorized - Invalid token' });
  }
}

// Irish Payroll Extraction Prompt
const IRISH_PAYROLL_PROMPT = `
You are an expert at extracting information from Irish payroll documents.

Analyze the provided image and extract all payroll information following Irish payroll standards.

Extract the following fields if present:
- Employee Name
- PPS Number (Personal Public Service Number)
- Pay Period (start and end dates)
- Payment Date
- Gross Pay
- PAYE (Pay As You Earn tax)
- PRSI (Pay Related Social Insurance)
- USC (Universal Social Charge)
- Other Deductions (itemize if possible)
- Net Pay
- Employer Name
- Employer Registration Number
- Tax Credits Applied
- Year to Date (YTD) figures for: Gross Pay, PAYE, PRSI, USC, Net Pay

Return the data in the following JSON format:
{
  "employee_name": "string",
  "pps_number": "string",
  "pay_period_start": "YYYY-MM-DD",
  "pay_period_end": "YYYY-MM-DD",
  "payment_date": "YYYY-MM-DD",
  "gross_pay": "number",
  "paye": "number",
  "prsi": "number",
  "usc": "number",
  "other_deductions": [
    {"description": "string", "amount": "number"}
  ],
  "net_pay": "number",
  "employer_name": "string",
  "employer_registration_number": "string",
  "tax_credits": "number",
  "ytd_gross_pay": "number",
  "ytd_paye": "number",
  "ytd_prsi": "number",
  "ytd_usc": "number",
  "ytd_net_pay": "number",
  "additional_info": {
    "key": "value"
  }
}

If a field is not found, use null. Be as accurate as possible.
Return ONLY valid JSON, no additional text.
`;

// Check cache before making API call
async function checkCache(userId, imageHash) {
  try {
    const cacheRef = db
      .collection('extraction_cache')
      .doc(`${userId}_${imageHash}`);

    const doc = await cacheRef.get();

    if (doc.exists) {
      const data = doc.data();
      const expiryTime = new Date(data.createdAt._seconds * 1000 + 24 * 60 * 60 * 1000);

      if (new Date() < expiryTime) {
        return data;
      }
    }
  } catch (error) {
    console.error('Cache check error:', error);
  }

  return null;
}

// Save to cache
async function saveToCache(userId, imageHash, result) {
  try {
    await db
      .collection('extraction_cache')
      .doc(`${userId}_${imageHash}`)
      .set({
        ...result,
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
      });
  } catch (error) {
    console.error('Cache save error:', error);
  }
}

// Log API usage for monitoring and cost tracking
async function logApiUsage(userId, success, tokensUsed = 0) {
  try {
    await db.collection('api_usage').add({
      userId,
      timestamp: admin.firestore.FieldValue.serverTimestamp(),
      success,
      tokensUsed,
      service: 'gemini',
    });
  } catch (error) {
    console.error('Usage logging error:', error);
  }
}

// Health check endpoint
app.get('/health', (req, res) => {
  res.json({ status: 'ok', timestamp: new Date().toISOString() });
});

// Extract payroll data from image
app.post('/api/v1/payroll/extract', verifyAuth, limiter, async (req, res) => {
  const startTime = Date.now();

  try {
    const { imageBase64, mimeType = 'image/jpeg' } = req.body;

    // Validation
    if (!imageBase64) {
      return res.status(400).json({ error: 'Missing imageBase64 parameter' });
    }

    // Check cache
    const imageHash = require('crypto')
      .createHash('md5')
      .update(imageBase64.substring(0, 1000))
      .digest('hex');

    const cached = await checkCache(req.user.uid, imageHash);

    if (cached) {
      console.log('Returning cached result');
      return res.json({
        ...cached,
        cached: true,
        processingTime: Date.now() - startTime,
      });
    }

    // Call Gemini API
    const model = genAI.getGenerativeModel({
      model: 'gemini-2.0-flash-exp',
      generationConfig: {
        temperature: 0.1,
        topK: 32,
        topP: 0.95,
        maxOutputTokens: 8192,
      },
    });

    const imagePart = {
      inlineData: {
        data: imageBase64,
        mimeType: mimeType,
      },
    };

    const result = await model.generateContent([IRISH_PAYROLL_PROMPT, imagePart]);
    const response = await result.response;
    const text = response.text();

    // Clean up JSON response
    let jsonText = text.trim();
    if (jsonText.startsWith('```json')) {
      jsonText = jsonText.replace(/```json\n?/g, '').replace(/\n?```$/g, '');
    } else if (jsonText.startsWith('```')) {
      jsonText = jsonText.replace(/```\n?/g, '').replace(/\n?```$/g, '');
    }

    const responseData = {
      extractedData: jsonText,
      rawText: text,
      confidence: 0.85,
      cached: false,
      processingTime: Date.now() - startTime,
    };

    // Save to cache
    await saveToCache(req.user.uid, imageHash, responseData);

    // Log usage
    await logApiUsage(req.user.uid, true, response.usageMetadata?.totalTokenCount || 0);

    res.json(responseData);
  } catch (error) {
    console.error('Extraction error:', error);

    // Log failed usage
    await logApiUsage(req.user.uid, false);

    res.status(500).json({
      error: 'Failed to extract payroll data',
      message: process.env.NODE_ENV === 'development' ? error.message : undefined,
    });
  }
});

// Get extraction history
app.get('/api/v1/payroll/history', verifyAuth, async (req, res) => {
  try {
    const limit = parseInt(req.query.limit) || 10;

    const snapshot = await db
      .collection('extraction_cache')
      .where('userId', '==', req.user.uid)
      .orderBy('createdAt', 'desc')
      .limit(limit)
      .get();

    const history = [];
    snapshot.forEach((doc) => {
      history.push({
        id: doc.id,
        ...doc.data(),
      });
    });

    res.json(history);
  } catch (error) {
    console.error('History error:', error);
    res.status(500).json({ error: 'Failed to fetch history' });
  }
});

// Get user's API usage statistics
app.get('/api/v1/usage/stats', verifyAuth, async (req, res) => {
  try {
    const snapshot = await db
      .collection('api_usage')
      .where('userId', '==', req.user.uid)
      .orderBy('timestamp', 'desc')
      .limit(100)
      .get();

    const stats = {
      totalRequests: snapshot.size,
      successfulRequests: 0,
      failedRequests: 0,
      totalTokens: 0,
    };

    snapshot.forEach((doc) => {
      const data = doc.data();
      if (data.success) {
        stats.successfulRequests++;
      } else {
        stats.failedRequests++;
      }
      stats.totalTokens += data.tokensUsed || 0;
    });

    res.json(stats);
  } catch (error) {
    console.error('Stats error:', error);
    res.status(500).json({ error: 'Failed to fetch stats' });
  }
});

// Error handling middleware
app.use((err, req, res, next) => {
  console.error('Unhandled error:', err);
  res.status(500).json({ error: 'Internal server error' });
});

// Start server
app.listen(PORT, () => {
  console.log(`Backend service running on port ${PORT}`);
  console.log(`Environment: ${process.env.NODE_ENV || 'development'}`);
});

module.exports = app;
