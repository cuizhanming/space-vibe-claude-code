# Quick Start Guide

## Prerequisites

Make sure you have:
- [x] Flutter SDK installed
- [x] Firebase CLI installed (`npm install -g firebase-tools`)
- [x] Firebase project created
- [ ] Firebase services enabled (follow steps below)

## Step 1: Enable Firebase Services in Console

### A. Enable Email/Password Authentication

1. Open [Firebase Console](https://console.firebase.google.com)
2. Select project: **flutter-payroll-scanner**
3. Go to **Build** → **Authentication**
4. Click **Get started** (if first time)
5. Click **Sign-in method** tab
6. Click **Email/Password**
7. Toggle **Enable** to ON
8. Click **Save**

### B. Create Firestore Database

1. Go to **Build** → **Firestore Database**
2. Click **Create database**
3. Choose **Start in production mode** (we'll deploy rules next)
4. Select location: **us-central** (or your preferred location)
5. Click **Enable**

### C. Create Firebase Storage

1. Go to **Build** → **Storage**
2. Click **Get started**
3. Choose **Start in production mode** (we'll deploy rules next)
4. Use the same location as Firestore
5. Click **Done**

## Step 2: Deploy Security Rules

Deploy the security rules from your terminal:

```bash
# Login to Firebase (if not already logged in)
firebase login

# Deploy Firestore rules and indexes
firebase deploy --only firestore

# Deploy Storage rules
firebase deploy --only storage

# Or deploy everything at once
firebase deploy
```

## Step 3: Run the App Locally

### Web (Chrome)

```bash
flutter run -d chrome --dart-define=GEMINI_API_KEY=your_gemini_api_key
```

The app will open at: http://127.0.0.1:XXXXX

### Desktop (macOS)

```bash
flutter run -d macos --dart-define=GEMINI_API_KEY=your_gemini_api_key
```

### Mobile (iOS Simulator)

```bash
flutter run -d ios --dart-define=GEMINI_API_KEY=your_gemini_api_key
```

### Mobile (Android Emulator)

```bash
flutter run -d android --dart-define=GEMINI_API_KEY=your_gemini_api_key
```

## Step 4: Test the App

### Register a New Account

1. Open the app
2. Click **Sign Up** or **Create Account**
3. Enter email and password
4. Click **Register**

### Login

1. Enter your email and password
2. Click **Login**
3. You should see the home screen

### Scan a Payroll Document

1. Click **Scan Document** or **Upload**
2. Choose an image or PDF of a payroll slip
3. The app will use Gemini AI to extract data
4. Review and save the extracted information

## Get Gemini API Key

1. Go to [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Click **Create API key**
3. Copy the API key
4. Use it when running the app with `--dart-define=GEMINI_API_KEY=your_key`

## Troubleshooting

### "Missing or invalid authentication code"

**Solution**: Make sure Email/Password authentication is enabled in Firebase Console.

### "Permission denied" on Firestore

**Solution**: Deploy Firestore rules using `firebase deploy --only firestore`

### Storage upload fails

**Solution**: Deploy Storage rules using `firebase deploy --only storage`

### Can't run `firebase deploy`

**Solution**: Install Firebase CLI: `npm install -g firebase-tools`

### Database errors on web

**Solution**: This is expected. Web uses Firestore instead of local SQLite database.

## Next Steps

1. ✅ Configure Firebase services (Authentication, Firestore, Storage)
2. ✅ Deploy security rules
3. ✅ Test the app locally
4. 📖 Read [FIREBASE_SETUP.md](./FIREBASE_SETUP.md) for detailed configuration
5. 🚀 Deploy to production (optional)

## Deployment to Production

### Build for Web

```bash
flutter build web --release --dart-define=GEMINI_API_KEY=your_key
```

### Deploy to Firebase Hosting

```bash
firebase deploy --only hosting
```

Your app will be available at: https://flutter-payroll-scanner.web.app

## Support

For detailed Firebase configuration, see [FIREBASE_SETUP.md](./FIREBASE_SETUP.md)

For app architecture, see [ARCHITECTURE.md](./ARCHITECTURE.md)
