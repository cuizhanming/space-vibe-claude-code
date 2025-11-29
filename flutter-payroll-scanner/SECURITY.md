# Production Secrets Management

## Security Architecture

This document outlines best practices for managing secrets in production Flutter apps.

## ⚠️ Critical Security Principles

1. **Never commit secrets to version control**
2. **Use different secrets for dev/staging/prod**
3. **Restrict API keys with platform/domain restrictions**
4. **Proxy sensitive API calls through your backend**
5. **Use compile-time vs runtime secrets appropriately**

## Solution Overview

### 1. Firebase Configuration (Low Risk)

Firebase config files (`google-services.json`, `GoogleService-Info.plist`) can be committed because:
- They're designed to be public
- Security is enforced via Firebase Security Rules
- API keys are restricted in Google Cloud Console

**Action Required**: Restrict Firebase API keys in [Google Cloud Console](https://console.cloud.google.com/apis/credentials)

### 2. Gemini API Key (HIGH RISK - Requires Backend Proxy)

**⚠️ NEVER expose Gemini API keys in client apps**

**Recommended: Backend Proxy Service**

```
Flutter App → Your Backend → Gemini API
           (authenticated)  (API key safe)
```

Benefits:
- API key never exposed to clients
- Rate limiting and abuse prevention
- User authentication/authorization
- Cost control and monitoring
- Response caching

### 3. User Tokens & Sensitive Data (Runtime)

Use Flutter Secure Storage for runtime secrets:
- Firebase auth tokens
- User session data
- Encrypted local preferences

## Implementation

### Option A: Backend Proxy (RECOMMENDED for Production)

Create a secure backend service to proxy Gemini API calls.

#### Backend Service (Node.js Example)

```javascript
// server.js
const express = require('express');
const { GoogleGenerativeAI } = require('@google/generative-ai');
const admin = require('firebase-admin');

const app = express();
app.use(express.json());

// Initialize Firebase Admin
admin.initializeApp();

// Initialize Gemini (server-side only)
const genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY);

// Middleware: Verify Firebase Auth Token
async function verifyAuth(req, res, next) {
  const token = req.headers.authorization?.split('Bearer ')[1];

  if (!token) {
    return res.status(401).json({ error: 'Unauthorized' });
  }

  try {
    const decodedToken = await admin.auth().verifyIdToken(token);
    req.user = decodedToken;
    next();
  } catch (error) {
    res.status(401).json({ error: 'Invalid token' });
  }
}

// Protected endpoint for payroll extraction
app.post('/api/extract-payroll', verifyAuth, async (req, res) => {
  try {
    const { imageBase64 } = req.body;

    // Rate limiting check
    // Cost tracking
    // Validation

    const model = genAI.getGenerativeModel({ model: 'gemini-2.0-flash-exp' });
    const result = await model.generateContent([
      prompt,
      { inlineData: { data: imageBase64, mimeType: 'image/jpeg' } }
    ]);

    res.json({ data: result.response.text() });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

app.listen(3000);
```

#### Flutter Service Update

```dart
// lib/core/services/gemini_service.dart
import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart';

class GeminiService {
  GeminiService({required Dio dio}) : _dio = dio;

  final Dio _dio;
  static const String _backendUrl = String.fromEnvironment(
    'BACKEND_URL',
    defaultValue: 'https://your-backend.com',
  );

  Future<Either<Failure, ExtractedPayrollData>> extractFromImage(
    File imageFile,
  ) async {
    try {
      // Get current user's auth token
      final user = FirebaseAuth.instance.currentUser;
      if (user == null) {
        return const Left(AuthFailure('User not authenticated'));
      }

      final token = await user.getIdToken();

      // Convert image to base64
      final bytes = await imageFile.readAsBytes();
      final base64Image = base64Encode(bytes);

      // Call backend proxy
      final response = await _dio.post(
        '$_backendUrl/api/extract-payroll',
        data: {'imageBase64': base64Image},
        options: Options(
          headers: {'Authorization': 'Bearer $token'},
        ),
      );

      return Right(
        ExtractedPayrollData(
          rawJson: response.data['data'],
          rawText: response.data['data'],
          confidence: 0.85,
        ),
      );
    } on DioException catch (e) {
      return Left(NetworkFailure('API call failed: ${e.message}'));
    } catch (e) {
      return Left(GeminiFailure('Extraction failed: $e'));
    }
  }
}
```

### Option B: Build-Time Secrets (Development/Testing Only)

For development and testing environments, use compile-time constants:

#### Using `--dart-define`

```bash
# Development
flutter run \
  --dart-define=GEMINI_API_KEY=dev_key_here \
  --dart-define=BACKEND_URL=http://localhost:3000

# Production (still not recommended for Gemini)
flutter build apk \
  --dart-define=BACKEND_URL=https://api.production.com \
  --dart-define=ENVIRONMENT=production
```

#### Access in Code

```dart
// lib/core/config/app_config.dart
class AppConfig {
  static const String backendUrl = String.fromEnvironment(
    'BACKEND_URL',
    defaultValue: 'http://localhost:3000',
  );

  static const String environment = String.fromEnvironment(
    'ENVIRONMENT',
    defaultValue: 'development',
  );

  // For development/testing only - NOT for production
  static const String geminiApiKey = String.fromEnvironment(
    'GEMINI_API_KEY',
    defaultValue: '',
  );

  static bool get isProduction => environment == 'production';
  static bool get isDevelopment => environment == 'development';
}
```

### Option C: Build Flavors (Multi-Environment)

#### Define Flavors

**Android** (`android/app/build.gradle`):

```gradle
android {
    flavorDimensions "environment"

    productFlavors {
        dev {
            dimension "environment"
            applicationIdSuffix ".dev"
            versionNameSuffix "-dev"
            resValue "string", "app_name", "Payroll Scanner Dev"
        }

        staging {
            dimension "environment"
            applicationIdSuffix ".staging"
            versionNameSuffix "-staging"
            resValue "string", "app_name", "Payroll Scanner Staging"
        }

        prod {
            dimension "environment"
            resValue "string", "app_name", "Payroll Scanner"
        }
    }
}
```

**iOS** (`ios/Runner/Info.plist`):

Use XCode schemes for different environments.

#### Environment-Specific Configuration

```dart
// lib/core/config/environment.dart
enum Environment { dev, staging, production }

class EnvironmentConfig {
  final Environment environment;
  final String backendUrl;
  final String appName;
  final bool enableLogging;

  const EnvironmentConfig({
    required this.environment,
    required this.backendUrl,
    required this.appName,
    required this.enableLogging,
  });

  static const dev = EnvironmentConfig(
    environment: Environment.dev,
    backendUrl: 'http://localhost:3000',
    appName: 'Payroll Scanner Dev',
    enableLogging: true,
  );

  static const staging = EnvironmentConfig(
    environment: Environment.staging,
    backendUrl: 'https://staging-api.example.com',
    appName: 'Payroll Scanner Staging',
    enableLogging: true,
  );

  static const production = EnvironmentConfig(
    environment: Environment.production,
    backendUrl: 'https://api.example.com',
    appName: 'Payroll Scanner',
    enableLogging: false,
  );
}
```

#### Entry Points

```dart
// lib/main_dev.dart
import 'package:flutter/material.dart';
import 'app.dart';
import 'core/config/environment.dart';

void main() {
  runApp(const PayrollScannerApp(config: EnvironmentConfig.dev));
}
```

```dart
// lib/main_staging.dart
import 'package:flutter/material.dart';
import 'app.dart';
import 'core/config/environment.dart';

void main() {
  runApp(const PayrollScannerApp(config: EnvironmentConfig.staging));
}
```

```dart
// lib/main_production.dart
import 'package:flutter/material.dart';
import 'app.dart';
import 'core/config/environment.dart';

void main() {
  runApp(const PayrollScannerApp(config: EnvironmentConfig.production));
}
```

#### Run with Flavors

```bash
# Development
flutter run -t lib/main_dev.dart --flavor dev

# Staging
flutter run -t lib/main_staging.dart --flavor staging

# Production
flutter build apk -t lib/main_production.dart --flavor prod --release
```

### Option D: CI/CD Secrets (GitHub Actions Example)

```yaml
# .github/workflows/build.yml
name: Build and Deploy

on:
  push:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v3

      - uses: subosito/flutter-action@v2
        with:
          flutter-version: '3.24.0'

      - name: Build APK
        env:
          BACKEND_URL: ${{ secrets.BACKEND_URL }}
          FIREBASE_OPTIONS: ${{ secrets.FIREBASE_OPTIONS }}
        run: |
          flutter build apk \
            --release \
            --dart-define=BACKEND_URL=$BACKEND_URL \
            --dart-define=ENVIRONMENT=production
```

### Runtime Secrets (Flutter Secure Storage)

```dart
// lib/core/services/secure_storage_service.dart
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class SecureStorageService {
  final FlutterSecureStorage _storage;

  SecureStorageService() : _storage = const FlutterSecureStorage(
    aOptions: AndroidOptions(
      encryptedSharedPreferences: true,
    ),
    iOptions: IOSOptions(
      accessibility: KeychainAccessibility.first_unlock,
    ),
  );

  // Save user authentication token
  Future<void> saveAuthToken(String token) async {
    await _storage.write(key: 'auth_token', value: token);
  }

  // Get user authentication token
  Future<String?> getAuthToken() async {
    return await _storage.read(key: 'auth_token');
  }

  // Delete all secure data
  Future<void> deleteAll() async {
    await _storage.deleteAll();
  }
}
```

## Security Checklist

### Before Production Deployment

- [ ] Remove all hardcoded API keys
- [ ] Implement backend proxy for Gemini API
- [ ] Set up Firebase Security Rules
- [ ] Configure API key restrictions in Google Cloud Console
- [ ] Use environment-specific configurations
- [ ] Enable Firebase App Check
- [ ] Implement rate limiting in backend
- [ ] Set up monitoring and alerting
- [ ] Use HTTPS for all API calls
- [ ] Implement certificate pinning (optional, advanced)
- [ ] Review and audit all third-party packages
- [ ] Enable ProGuard/R8 (Android) and obfuscation (iOS)
- [ ] Test with production-like data

### Google Cloud API Key Restrictions

1. Go to [Google Cloud Console](https://console.cloud.google.com/apis/credentials)
2. Select your Gemini API key
3. Add restrictions:
   - **Application restrictions**: HTTP referrers (for web) or Android/iOS apps
   - **API restrictions**: Limit to Generative Language API
   - **Quotas**: Set reasonable limits

### Firebase Security Rules

```javascript
// Firestore rules
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    function isAuthenticated() {
      return request.auth != null;
    }

    function isOwner(userId) {
      return request.auth.uid == userId;
    }

    match /payroll_documents/{docId} {
      allow read, write: if isAuthenticated()
        && isOwner(resource.data.userId);
    }

    match /payroll_items/{itemId} {
      allow read, write: if isAuthenticated();
    }
  }
}
```

## Recommended Production Architecture

```
┌─────────────────┐
│  Flutter App    │
│  (No API Keys)  │
└────────┬────────┘
         │ HTTPS + Auth Token
         │
┌────────▼────────────────┐
│   Backend Service       │
│   (API Keys Secured)    │
│   - Authentication      │
│   - Rate Limiting       │
│   - Logging/Monitoring  │
└────────┬────────────────┘
         │
    ┌────┴─────┐
    │          │
┌───▼──┐   ┌──▼─────┐
│Gemini│   │Firebase│
│  AI  │   │        │
└──────┘   └────────┘
```

## Cost Optimization

1. **Cache responses** in Firestore to avoid redundant API calls
2. **Implement request throttling** on client and server
3. **Use Cloud Functions** with Firebase for serverless backend
4. **Monitor API usage** and set budget alerts
5. **Compress images** before sending to API

## Additional Resources

- [Flutter Security Best Practices](https://flutter.dev/security)
- [Firebase Security Rules](https://firebase.google.com/docs/rules)
- [OWASP Mobile Security](https://owasp.org/www-project-mobile-security/)
- [Google Cloud Secret Manager](https://cloud.google.com/secret-manager)
