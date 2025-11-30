# Firebase Setup Guide for Flutter Payroll Scanner

This guide will help you configure Firebase services for the Flutter Payroll Scanner app.

## Prerequisites

- Firebase project created: `flutter-payroll-scanner`
- Firebase configuration already added to the app
- Access to [Firebase Console](https://console.firebase.google.com)

## 1. Enable Firebase Authentication

### Email/Password Authentication (Required)

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Select your project: **flutter-payroll-scanner**
3. Navigate to **Build** → **Authentication** → **Sign-in method**
4. Click on **Email/Password** provider
5. Toggle **Enable** to ON
6. Click **Save**

### Google Sign-In (Optional)

1. In **Authentication** → **Sign-in method**
2. Click on **Google** provider
3. Toggle **Enable** to ON
4. Enter your project support email
5. Click **Save**

### Authorized Domains

1. In **Authentication** → **Settings** → **Authorized domains**
2. Ensure these domains are authorized:
   - `localhost` (for local development)
   - `127.0.0.1` (for local development)
   - Your production domain (when you deploy)

## 2. Set Up Cloud Firestore

### Enable Firestore Database

1. Navigate to **Build** → **Firestore Database**
2. Click **Create database**
3. Choose **Start in test mode** (we'll secure it next)
4. Select your preferred location (e.g., `us-central`)
5. Click **Enable**

### Configure Security Rules

1. In **Firestore Database** → **Rules** tab
2. Replace the default rules with the following:

```javascript
rules_version = '2';

service cloud.firestore {
  match /databases/{database}/documents {
    // Helper function to check if user is authenticated
    function isAuthenticated() {
      return request.auth != null;
    }

    // Helper function to check if user owns the document
    function isOwner(userId) {
      return isAuthenticated() && request.auth.uid == userId;
    }

    // User profiles - users can only read/write their own profile
    match /users/{userId} {
      allow read, write: if isOwner(userId);
    }

    // Payroll documents - users can only access their own documents
    match /payroll_documents/{documentId} {
      allow read, write: if isAuthenticated() &&
                             resource.data.userId == request.auth.uid;
      allow create: if isAuthenticated() &&
                       request.resource.data.userId == request.auth.uid;
    }

    // Payroll items - users can only access items from their documents
    match /payroll_items/{itemId} {
      allow read, write: if isAuthenticated() &&
                             resource.data.userId == request.auth.uid;
      allow create: if isAuthenticated() &&
                       request.resource.data.userId == request.auth.uid;
    }
  }
}
```

3. Click **Publish**

## 3. Set Up Firebase Storage

### Enable Storage

1. Navigate to **Build** → **Storage**
2. Click **Get started**
3. Choose **Start in test mode** (we'll secure it next)
4. Select the same location as Firestore
5. Click **Done**

### Configure Storage Rules

1. In **Storage** → **Rules** tab
2. Replace the default rules with:

```javascript
rules_version = '2';

service firebase.storage {
  match /b/{bucket}/o {
    // Helper function to check if user is authenticated
    function isAuthenticated() {
      return request.auth != null;
    }

    // Helper function to validate file size (max 10MB for documents)
    function isValidSize() {
      return request.resource.size < 10 * 1024 * 1024;
    }

    // Helper function to validate file types for payroll documents
    function isValidDocumentType() {
      return request.resource.contentType.matches('image/.*') ||
             request.resource.contentType == 'application/pdf';
    }

    // User payroll documents - users can only access their own files
    match /payroll_documents/{userId}/{allPaths=**} {
      allow read: if isAuthenticated() && request.auth.uid == userId;
      allow write: if isAuthenticated() &&
                      request.auth.uid == userId &&
                      isValidSize() &&
                      isValidDocumentType();
      allow delete: if isAuthenticated() && request.auth.uid == userId;
    }

    // User profile images
    match /profile_images/{userId}/{allPaths=**} {
      allow read: if isAuthenticated();
      allow write: if isAuthenticated() &&
                      request.auth.uid == userId &&
                      request.resource.size < 5 * 1024 * 1024 &&
                      request.resource.contentType.matches('image/.*');
    }
  }
}
```

3. Click **Publish**

## 4. Create Firestore Indexes (Optional but Recommended)

For better query performance, create these indexes:

1. Navigate to **Firestore Database** → **Indexes** tab
2. Click **Create index**

### Index for Payroll Documents by Date

- Collection ID: `payroll_documents`
- Fields to index:
  - `userId` - Ascending
  - `paymentDate` - Descending
- Query scope: Collection
- Click **Create**

### Index for Payroll Documents by Period

- Collection ID: `payroll_documents`
- Fields to index:
  - `userId` - Ascending
  - `payPeriod` - Descending
- Query scope: Collection
- Click **Create**

## 5. Enable Firebase Services in Google Cloud

### Enable Required APIs

1. Go to [Google Cloud Console](https://console.cloud.google.com)
2. Select project: `flutter-payroll-scanner`
3. Navigate to **APIs & Services** → **Library**
4. Search and enable:
   - Cloud Firestore API
   - Firebase Authentication API
   - Cloud Storage for Firebase API
   - Firebase Management API

## 6. Testing the Setup

### Test Authentication

1. Run the Flutter app on web: `flutter run -d chrome`
2. Open browser at: http://127.0.0.1:65375
3. Try to register a new account with email/password
4. Verify you can login successfully

### Test Firestore

1. After logging in, check the **Firestore Database** in Firebase Console
2. You should see collections being created:
   - `users/{userId}`
   - `payroll_documents/{documentId}`

### Test Storage

1. Upload a payroll document in the app
2. Check **Storage** in Firebase Console
3. You should see files under `payroll_documents/{userId}/`

## 7. Environment Variables

The app uses the following environment variable for Gemini AI:

```bash
flutter run -d chrome --dart-define=GEMINI_API_KEY=your_api_key_here
```

To get a Gemini API key:
1. Go to [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Create a new API key
3. Use it when running the app

## 8. Production Deployment Checklist

When deploying to production:

- [ ] Update Firestore security rules to remove test mode
- [ ] Update Storage security rules to remove test mode
- [ ] Add your production domain to authorized domains
- [ ] Enable Firebase App Check for additional security
- [ ] Set up Firebase Hosting (optional)
- [ ] Configure CORS for Storage if needed
- [ ] Review and adjust rate limits
- [ ] Set up Firebase Analytics (optional)
- [ ] Configure environment-specific Firebase projects

## Troubleshooting

### "Missing or invalid authentication code"

- Ensure Email/Password authentication is enabled
- Check that `localhost` is in authorized domains
- Clear browser cache and cookies
- Check Firebase Console for any errors

### "Permission denied" on Firestore

- Verify security rules are published
- Ensure userId matches authenticated user
- Check browser console for detailed error messages

### Storage upload fails

- Verify file size is under 10MB
- Check file type is image or PDF
- Ensure Storage rules are published
- Verify user is authenticated

### Database errors on web

- The local SQLite database doesn't work on web
- All data is stored in Firestore when running on web
- This is expected behavior and handled by the app

## Support

For more information:
- [Firebase Documentation](https://firebase.google.com/docs)
- [FlutterFire Documentation](https://firebase.flutter.dev)
- [Gemini AI Documentation](https://ai.google.dev/docs)
