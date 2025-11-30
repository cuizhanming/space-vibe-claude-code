# Deployment Guide

Complete guide for deploying the Payroll Scanner application to production.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Environment Setup](#environment-setup)
3. [CI/CD Pipeline Configuration](#cicd-pipeline-configuration)
4. [Backend Deployment](#backend-deployment)
5. [Flutter App Deployment](#flutter-app-deployment)
6. [Monitoring & Maintenance](#monitoring--maintenance)
7. [Troubleshooting](#troubleshooting)

## Prerequisites

### Required Accounts & Services

- [ ] Google Cloud Platform account (for Cloud Run, Firebase)
- [ ] Firebase project created
- [ ] GitHub repository
- [ ] Apple Developer Account (for iOS)
- [ ] Google Play Console account (for Android)
- [ ] Domain name (optional, for custom domain)

### Required Tools

```bash
# Flutter SDK
flutter --version  # 3.2.0+

# Node.js
node --version     # 20+

# Docker (for backend)
docker --version   # 24+

# gcloud CLI
gcloud --version

# Firebase CLI
firebase --version
```

## Environment Setup

### 1. Firebase Setup

```bash
# Install Firebase CLI
npm install -g firebase-tools

# Login to Firebase
firebase login

# Initialize Firebase in project
cd flutter-payroll-scanner
firebase init

# Select:
# - Firestore
# - Functions (if using Cloud Functions)
# - Hosting (for web)
# - Storage
```

### 2. Google Cloud Setup

```bash
# Login to Google Cloud
gcloud auth login

# Set project
gcloud config set project YOUR_PROJECT_ID

# Enable required APIs
gcloud services enable \
  run.googleapis.com \
  cloudbuild.googleapis.com \
  secretmanager.googleapis.com \
  firestore.googleapis.com \
  firebase.googleapis.com
```

### 3. Create Service Accounts

```bash
# Create service account for CI/CD
gcloud iam service-accounts create github-actions \
  --display-name="GitHub Actions Service Account"

# Grant necessary roles
gcloud projects add-iam-policy-binding YOUR_PROJECT_ID \
  --member="serviceAccount:github-actions@YOUR_PROJECT_ID.iam.gserviceaccount.com" \
  --role="roles/run.admin"

gcloud projects add-iam-policy-binding YOUR_PROJECT_ID \
  --member="serviceAccount:github-actions@YOUR_PROJECT_ID.iam.gserviceaccount.com" \
  --role="roles/iam.serviceAccountUser"

# Generate key file
gcloud iam service-accounts keys create key.json \
  --iam-account=github-actions@YOUR_PROJECT_ID.iam.gserviceaccount.com
```

### 4. Store Secrets in Google Cloud Secret Manager

```bash
# Create secrets
echo -n "YOUR_GEMINI_API_KEY" | gcloud secrets create gemini-api-key --data-file=-
echo -n "YOUR_PROJECT_ID" | gcloud secrets create firebase-project-id --data-file=-

# Grant access to Cloud Run service account
gcloud secrets add-iam-policy-binding gemini-api-key \
  --member="serviceAccount:YOUR_PROJECT_NUMBER-compute@developer.gserviceaccount.com" \
  --role="roles/secretmanager.secretAccessor"
```

## CI/CD Pipeline Configuration

### 1. GitHub Secrets Setup

Add these secrets to your GitHub repository (Settings → Secrets and variables → Actions):

**Firebase Secrets:**
```
FIREBASE_SERVICE_ACCOUNT    # Firebase service account JSON
FIREBASE_PROJECT_ID         # Your Firebase project ID
FIREBASE_TOKEN              # Firebase CLI token (run: firebase login:ci)
```

**Google Cloud Secrets:**
```
GCP_SA_KEY_DEV             # Service account key for development
GCP_SA_KEY_STAGING         # Service account key for staging
GCP_SA_KEY_PROD            # Service account key for production
```

**Backend URLs:**
```
DEV_BACKEND_URL            # https://backend-dev-xxx.run.app
STAGING_BACKEND_URL        # https://backend-staging-xxx.run.app
PROD_BACKEND_URL           # https://api.yourdomain.com
```

**Android Signing (Production):**
```
ANDROID_KEYSTORE_BASE64    # Base64 encoded keystore file
ANDROID_KEYSTORE_PASSWORD  # Keystore password
ANDROID_KEY_ALIAS          # Key alias
ANDROID_KEY_PASSWORD       # Key password
```

**Notifications (Optional):**
```
SLACK_WEBHOOK_URL          # Slack webhook for notifications
```

### 2. Generate Android Keystore

```bash
# Generate keystore
keytool -genkey -v -keystore keystore.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias payroll-scanner

# Convert to base64 for GitHub Secrets
base64 keystore.jks > keystore.base64.txt
# Copy contents to ANDROID_KEYSTORE_BASE64 secret
```

### 3. Branch Strategy

```
main       → Production (requires approval)
staging    → Staging (auto-deploy)
develop    → Development (auto-deploy)
feature/*  → Feature branches (CI only, no deploy)
```

## Backend Deployment

### Option 1: Google Cloud Run (Recommended)

#### Initial Setup

```bash
cd backend

# Build Docker image
docker build -t payroll-backend .

# Test locally
docker run -p 3000:3000 \
  -e GEMINI_API_KEY=your_key \
  -e FIREBASE_PROJECT_ID=your_project \
  payroll-backend

# Test health endpoint
curl http://localhost:3000/health
```

#### Deploy to Cloud Run

```bash
# Tag image
docker tag payroll-backend gcr.io/YOUR_PROJECT_ID/backend:latest

# Push to Container Registry
docker push gcr.io/YOUR_PROJECT_ID/backend:latest

# Deploy to Cloud Run
gcloud run deploy backend-prod \
  --image gcr.io/YOUR_PROJECT_ID/backend:latest \
  --platform managed \
  --region europe-west2 \
  --allow-unauthenticated \
  --set-env-vars="NODE_ENV=production" \
  --set-secrets="GEMINI_API_KEY=gemini-api-key:latest,FIREBASE_PROJECT_ID=firebase-project-id:latest" \
  --memory 512Mi \
  --cpu 2 \
  --min-instances 1 \
  --max-instances 100 \
  --concurrency 80 \
  --timeout 60s

# Get service URL
gcloud run services describe backend-prod \
  --platform managed \
  --region us-central1 \
  --format 'value(status.url)'
```

#### Setup Custom Domain

```bash
# Map custom domain
gcloud run domain-mappings create \
  --service backend-prod \
  --domain api.yourdomain.com \
  --region europe-west2

# Add DNS records as instructed by gcloud output
```

### Option 2: Firebase Cloud Functions

```bash
cd backend

# Install Firebase Functions
npm install -g firebase-tools
firebase init functions

# Deploy
firebase deploy --only functions

# Set environment variables
firebase functions:config:set \
  gemini.api_key="YOUR_GEMINI_API_KEY" \
  firebase.project_id="YOUR_PROJECT_ID"

# Redeploy with config
firebase deploy --only functions
```

### Option 3: AWS (Alternative)

```bash
# Using AWS Elastic Beanstalk
eb init -p node.js payroll-backend
eb create payroll-backend-prod
eb deploy

# Or using AWS Lambda with API Gateway
serverless deploy --stage prod
```

## Flutter App Deployment

### Android Deployment

#### 1. Prepare for Release

```bash
# Build app bundle
flutter build appbundle \
  -t lib/main_production.dart \
  --dart-define=BACKEND_URL=https://api.yourdomain.com \
  --dart-define=ENVIRONMENT=production \
  --release

# Output: build/app/outputs/bundle/release/app-release.aab
```

#### 2. Upload to Google Play Console

1. Go to [Google Play Console](https://play.google.com/console)
2. Create new app (if first time)
3. Complete store listing
4. Upload AAB to Internal Testing track
5. Promote to Production when ready

#### 3. Automated Deployment with GitHub Actions

The CI/CD pipeline automatically builds and can deploy to Play Console:

```yaml
# Add to .github/workflows/flutter-ci.yml
- name: Upload to Play Store
  uses: r0adkll/upload-google-play@v1
  with:
    serviceAccountJsonPlainText: ${{ secrets.PLAY_STORE_SERVICE_ACCOUNT }}
    packageName: com.spacevibe.payrollScanner
    releaseFiles: build/app/outputs/bundle/release/app-release.aab
    track: internal
```

### iOS Deployment

#### 1. Prepare for Release

```bash
# Build iOS
flutter build ios \
  -t lib/main_production.dart \
  --dart-define=BACKEND_URL=https://api.yourdomain.com \
  --dart-define=ENVIRONMENT=production \
  --release

# Open in Xcode for signing and upload
open ios/Runner.xcworkspace
```

#### 2. Upload to App Store Connect

1. In Xcode: Product → Archive
2. Validate archive
3. Upload to App Store Connect
4. Submit for review

#### 3. Automated with Fastlane

```bash
# Install Fastlane
gem install fastlane

# Initialize
cd ios
fastlane init

# Create Fastfile
# Then run
fastlane beta  # TestFlight
fastlane release  # App Store
```

### Web Deployment

#### Deploy to Firebase Hosting

```bash
# Build web
flutter build web \
  -t lib/main_production.dart \
  --dart-define=BACKEND_URL=https://api.yourdomain.com \
  --dart-define=ENVIRONMENT=production \
  --release

# Deploy to Firebase Hosting
firebase deploy --only hosting

# Or deploy to specific site
firebase deploy --only hosting:payroll-scanner-prod
```

#### Deploy to Netlify/Vercel

```bash
# Build
flutter build web --release

# Deploy to Netlify
netlify deploy --dir=build/web --prod

# Or to Vercel
vercel --prod build/web
```

### Desktop Deployment

#### macOS

```bash
# Build
flutter build macos --release

# Create DMG
npm install -g appdmg
appdmg appdmg.json payroll-scanner.dmg

# Notarize (requires Apple Developer account)
xcrun altool --notarize-app \
  --primary-bundle-id com.spacevibe.payrollScanner \
  --username your@email.com \
  --password @keychain:AC_PASSWORD \
  --file payroll-scanner.dmg
```

#### Windows

```bash
# Build
flutter build windows --release

# Create installer with Inno Setup
iscc installer-script.iss
```

#### Linux

```bash
# Build
flutter build linux --release

# Create .deb package
dpkg-deb --build payroll-scanner
```

## Monitoring & Maintenance

### Setup Cloud Monitoring

```bash
# Create uptime check
gcloud monitoring uptime create api-uptime-check \
  --resource-type=uptime-url \
  --host=api.yourdomain.com \
  --path=/health

# Create alert policy
gcloud alpha monitoring policies create \
  --notification-channels=CHANNEL_ID \
  --display-name="Backend API Down" \
  --condition-display-name="Health check failed" \
  --condition-threshold-value=1 \
  --condition-threshold-duration=300s
```

### Setup Error Tracking

```bash
# Install Sentry
npm install @sentry/node

# Add to server.js
const Sentry = require('@sentry/node');
Sentry.init({ dsn: process.env.SENTRY_DSN });
```

### View Logs

```bash
# Cloud Run logs
gcloud run services logs read backend-prod \
  --region us-central1 \
  --limit 100

# Or use Cloud Console
# https://console.cloud.google.com/logs
```

## Troubleshooting

### Common Issues

**1. Build fails on GitHub Actions**
```bash
# Check Flutter version matches
# Ensure all secrets are set
# Verify code generation completed
```

**2. Backend deployment fails**
```bash
# Check service account permissions
# Verify secrets exist in Secret Manager
# Check Cloud Run logs for errors
```

**3. App can't connect to backend**
```bash
# Verify BACKEND_URL is correct
# Check CORS settings
# Ensure backend is allowing unauthenticated access (or proper auth)
```

**4. High API costs**
```bash
# Check caching is working
# Review rate limiting configuration
# Monitor API usage in Cloud Console
```

### Rollback Procedure

```bash
# Rollback Cloud Run to previous revision
gcloud run services update-traffic backend-prod \
  --to-revisions=PREVIOUS_REVISION=100 \
  --region europe-west2

# Rollback Play Store release
# Use Play Console to halt rollout or rollback
```

## Post-Deployment Checklist

- [ ] Verify backend health endpoint responding
- [ ] Test authentication flow
- [ ] Upload and extract a test document
- [ ] Check logs for errors
- [ ] Verify monitoring and alerts working
- [ ] Test on multiple devices
- [ ] Review and set API quotas
- [ ] Enable Firebase App Check
- [ ] Setup backup strategy
- [ ] Document runbook for on-call
- [ ] Review security audit logs

## Useful Commands

```bash
# View Cloud Run services
gcloud run services list

# Describe service
gcloud run services describe backend-prod --region europe-west2

# Update service with new environment variable
gcloud run services update backend-prod \
  --update-env-vars KEY=VALUE \
  --region europe-west2

# View recent deployments
gcloud run revisions list --service backend-prod

# Stream logs
gcloud run services logs tail backend-prod

# Firebase deploy with custom target
firebase deploy --only hosting:prod

# Check Flutter build size
flutter build apk --analyze-size

# Generate release notes
git log --oneline v1.0.0..HEAD > CHANGELOG.md
```

## Support & Resources

- [Flutter Deployment Docs](https://flutter.dev/deployment)
- [Cloud Run Docs](https://cloud.google.com/run/docs)
- [Firebase Docs](https://firebase.google.com/docs)
- [GitHub Actions Docs](https://docs.github.com/actions)
