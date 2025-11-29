# Backend Proxy Service for Payroll Scanner

This is a reference implementation for a secure backend service that proxies requests to the Gemini API.

## Why a Backend Proxy?

**Security**: API keys are never exposed to client applications
**Control**: Implement rate limiting, authentication, and monitoring
**Cost**: Track and limit API usage per user
**Caching**: Cache responses to reduce API calls
**Analytics**: Monitor usage patterns and errors

## Architecture

```
Flutter App → Backend Service → Gemini API
           (authenticated)   (API key secure)
```

## Technology Options

Choose based on your infrastructure:

- **Node.js/Express** - Simple, fast, good for serverless
- **Firebase Cloud Functions** - Integrated with Firebase, serverless
- **Python/FastAPI** - Great for data processing
- **Java/Spring Boot** - Enterprise-grade, highly scalable

## Quick Start (Node.js)

See `server.js` for a complete implementation.

### Install Dependencies

```bash
npm install express @google/generative-ai firebase-admin dotenv cors helmet
```

### Environment Variables

Create `.env`:

```env
GEMINI_API_KEY=your_gemini_api_key_here
FIREBASE_PROJECT_ID=your_firebase_project_id
PORT=3000
NODE_ENV=production
```

### Run

```bash
# Development
npm run dev

# Production
npm start
```

## Firebase Cloud Functions Deployment

See `functions/index.js` for Cloud Functions implementation.

### Deploy

```bash
cd functions
npm install
firebase deploy --only functions
```

### Update Flutter App

```dart
// Update backend URL to your Cloud Function
const backendUrl = 'https://us-central1-your-project.cloudfunctions.net';
```

## API Endpoints

### POST /api/v1/payroll/extract

Extract payroll data from image.

**Request**:
```json
{
  "imageBase64": "base64_encoded_image",
  "mimeType": "image/jpeg"
}
```

**Response**:
```json
{
  "extractedData": "{...json...}",
  "rawText": "full response text",
  "confidence": 0.85,
  "cached": false
}
```

### POST /api/v1/payroll/extract-pdf

Extract from PDF (converts to images first).

### GET /api/v1/payroll/history

Get extraction history (requires Firestore).

## Security Best Practices

1. **Authentication**: Verify Firebase ID tokens
2. **Rate Limiting**: Prevent abuse
3. **Input Validation**: Validate all inputs
4. **Error Handling**: Don't expose internal errors
5. **Logging**: Log all requests for monitoring
6. **HTTPS Only**: Use SSL in production
7. **CORS**: Configure allowed origins

## Monitoring

- Track API usage and costs
- Monitor response times
- Alert on errors
- Track user patterns

## Cost Optimization

1. **Cache responses** in Firestore
2. **Set rate limits** per user
3. **Implement quotas** monthly/daily limits
4. **Compress images** before sending to Gemini
5. **Use cheaper models** for simple extractions

## Scaling

- **Horizontal**: Add more instances
- **Caching**: Redis or Memcached
- **CDN**: For static assets
- **Database**: Index frequently queried fields
- **Queue**: Background processing for large files
