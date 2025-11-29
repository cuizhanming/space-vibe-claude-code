# Payroll Scanner

A Flutter application for scanning and processing Irish payroll documents using Firebase and Google Gemini AI.

## Features

- 📱 **Multi-platform Support**: iOS, Android, macOS, Windows, and Linux
- 🔐 **Secure Authentication**: Firebase Authentication with email/password
- 📄 **Document Processing**: Scan or upload payroll images/PDFs
- 🤖 **AI-Powered Extraction**: Extract payroll data using Google Gemini AI
- 💾 **Offline Storage**: Local SQLite database with Drift
- ☁️ **Cloud Sync**: Firestore for remote data synchronization
- 🇮🇪 **Irish Payroll Support**: Specialized for Irish payroll documents (PAYE, PRSI, USC, PPS numbers)

## Architecture

This project follows Clean Architecture with a feature-first approach:

```
lib/
├── core/
│   ├── config/         # Firebase and app configuration
│   ├── database/       # Drift database tables
│   ├── errors/         # Error handling and failures
│   ├── router/         # GoRouter navigation
│   └── services/       # Core services (Database, Firestore, Gemini)
├── features/
│   ├── auth/           # Authentication feature
│   │   ├── data/       # Repository implementations
│   │   ├── domain/     # Entities, repositories, use cases
│   │   └── presentation/  # Pages, widgets, providers
│   ├── home/           # Home screen
│   └── payroll/        # Payroll document processing
└── shared/
    ├── theme/          # App theming
    └── widgets/        # Shared widgets
```

## Prerequisites

- Flutter SDK 3.2.0 or higher
- Dart SDK 3.2.0 or higher
- A Firebase project
- Google Gemini API key

## Setup Instructions

### 1. Install Flutter

Follow the official Flutter installation guide for your platform:
- [Flutter Installation](https://flutter.dev/docs/get-started/install)

### 2. Clone the Repository

```bash
cd flutter-payroll-scanner
```

### 3. Install Dependencies

```bash
flutter pub get
```

### 4. Firebase Setup

#### Option A: Using FlutterFire CLI (Recommended)

1. Install FlutterFire CLI:
```bash
dart pub global activate flutterfire_cli
```

2. Configure Firebase:
```bash
flutterfire configure
```

This will:
- Create a Firebase project (or select existing)
- Register your app for all platforms
- Generate `lib/core/config/firebase_options.dart` with your configuration

#### Option B: Manual Configuration

1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)

2. Enable the following services:
   - Authentication (Email/Password)
   - Cloud Firestore
   - Cloud Storage

3. Register your app for each platform and download configuration files:
   - **Android**: Download `google-services.json` → `android/app/`
   - **iOS**: Download `GoogleService-Info.plist` → `ios/Runner/`
   - **Web/Desktop**: Copy configuration values to `lib/core/config/firebase_options.dart`

4. Update `lib/core/config/firebase_options.dart` with your Firebase configuration

### 5. Gemini API Setup

1. Get your Gemini API key from [Google AI Studio](https://makersuite.google.com/app/apikey)

2. Add the API key to your environment:

**Option A: Environment Variable (Recommended)**
```bash
export GEMINI_API_KEY="your-api-key-here"
```

**Option B: Update the code**
Edit `lib/core/services/gemini_service.dart` and replace:
```dart
const apiKey = String.fromEnvironment(
  'GEMINI_API_KEY',
  defaultValue: 'YOUR_GEMINI_API_KEY_HERE',
);
```

with your actual API key (not recommended for production).

### 6. Generate Code

Run build_runner to generate required code:

```bash
flutter pub run build_runner build --delete-conflicting-outputs
```

This generates:
- Drift database code (`*.g.dart`)
- Freezed models (`*.freezed.dart`)
- Riverpod providers (`*.g.dart`)

### 7. Platform-Specific Setup

#### Android

Minimum SDK: 21 (Android 5.0)

1. Update `android/app/build.gradle` if needed:
```gradle
minSdkVersion 21
compileSdkVersion 34
targetSdkVersion 34
```

#### iOS

Minimum iOS version: 12.0

1. Update `ios/Podfile`:
```ruby
platform :ios, '12.0'
```

2. Install CocoaPods:
```bash
cd ios
pod install
cd ..
```

#### macOS

Minimum macOS version: 10.14

1. Enable required entitlements in `macos/Runner/DebugProfile.entitlements`:
```xml
<key>com.apple.security.network.client</key>
<true/>
<key>com.apple.security.files.user-selected.read-write</key>
<true/>
```

#### Windows

- Windows 10 version 1809 or higher

#### Linux

- Dependencies: GTK 3.0 or higher

## Running the App

### Development Mode

```bash
# Run on connected device/emulator
flutter run

# Run on specific platform
flutter run -d chrome        # Web
flutter run -d macos         # macOS
flutter run -d windows       # Windows
flutter run -d linux         # Linux
```

### Production Build

```bash
# Android
flutter build apk --release
flutter build appbundle --release

# iOS
flutter build ios --release

# macOS
flutter build macos --release

# Windows
flutter build windows --release

# Linux
flutter build linux --release

# Web
flutter build web --release
```

## Configuration

### Firebase Security Rules

#### Firestore Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /payroll_documents/{document} {
      allow read, write: if request.auth != null
        && request.auth.uid == resource.data.userId;
    }

    match /payroll_items/{item} {
      allow read, write: if request.auth != null;
    }
  }
}
```

#### Storage Rules

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /payroll_documents/{userId}/{allPaths=**} {
      allow read, write: if request.auth != null
        && request.auth.uid == userId;
    }
  }
}
```

### Environment Variables

Create a `.env` file (not committed to version control):

```env
GEMINI_API_KEY=your_gemini_api_key_here
FIREBASE_API_KEY=your_firebase_api_key
```

## Project Dependencies

### Core Dependencies
- `firebase_core` - Firebase initialization
- `firebase_auth` - User authentication
- `cloud_firestore` - Cloud database
- `firebase_storage` - File storage
- `google_generative_ai` - Gemini AI integration

### State Management
- `flutter_riverpod` - State management
- `riverpod_annotation` - Code generation for Riverpod

### Local Database
- `drift` - SQLite ORM
- `sqlite3_flutter_libs` - SQLite bindings
- `path_provider` - File system paths

### Document Processing
- `file_picker` - File selection
- `image_picker` - Camera/gallery access
- `pdf` - PDF generation
- `printing` - PDF rendering

### UI & Navigation
- `go_router` - Declarative routing
- `google_fonts` - Custom fonts
- `flutter_svg` - SVG support

### Utilities
- `dartz` - Functional programming (Either)
- `freezed` - Immutable models
- `logger` - Logging
- `intl` - Internationalization

## Testing

```bash
# Run all tests
flutter test

# Run with coverage
flutter test --coverage

# Run integration tests
flutter test integration_test
```

## Code Generation

When you modify models, providers, or database schemas:

```bash
# Watch for changes (development)
flutter pub run build_runner watch

# One-time generation
flutter pub run build_runner build --delete-conflicting-outputs
```

## Troubleshooting

### Common Issues

1. **Firebase initialization error**
   - Ensure `firebase_options.dart` is properly configured
   - Check that Firebase services are enabled in console

2. **Gemini API errors**
   - Verify API key is correct
   - Check API quota limits
   - Ensure billing is enabled on Google Cloud

3. **Build errors after adding dependencies**
   - Run `flutter clean`
   - Run `flutter pub get`
   - Run code generation again

4. **Platform-specific build errors**
   - Android: Check `minSdkVersion` and `google-services.json`
   - iOS: Run `pod install` in ios folder
   - macOS: Check entitlements and signing

### Debug Mode

Enable verbose logging:

```dart
// In main.dart
final logger = Logger(
  level: Level.verbose,
);
```

## Irish Payroll Support

This app is specialized for Irish payroll documents and extracts:

- **Employee Information**: Name, PPS Number
- **Pay Details**: Gross Pay, Net Pay, Pay Period
- **Deductions**:
  - PAYE (Pay As You Earn)
  - PRSI (Pay Related Social Insurance)
  - USC (Universal Social Charge)
- **Year to Date Figures**: All cumulative amounts
- **Employer Information**: Name, Registration Number

### PPS Number Validation

The app validates Irish PPS numbers (format: 7 digits + 1-2 letters).

## Best Practices

1. **Security**
   - Never commit API keys or Firebase config to version control
   - Use environment variables for sensitive data
   - Implement proper Firestore security rules

2. **Performance**
   - Use local database for offline support
   - Sync to Firestore when online
   - Compress images before upload

3. **Code Quality**
   - Follow Flutter style guide
   - Use linting rules in `analysis_options.yaml`
   - Write unit tests for business logic

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Run tests and linting
5. Submit a pull request

## License

[Your License Here]

## Support

For issues and questions:
- Create an issue in the repository
- Contact: [your-email@example.com]

## Roadmap

- [ ] Implement full document processing pipeline
- [ ] Add OCR for scanned documents
- [ ] Support multiple languages
- [ ] Export to PDF/Excel
- [ ] Data analytics dashboard
- [ ] Batch document processing
- [ ] Document search and filtering
- [ ] Dark mode support (already implemented in theme)
