# CLAUDE.md — flutter-payroll-scanner

Cross-platform Flutter app for scanning Irish payroll documents using Gemini AI.

## Session Start

1. Check Dart/Flutter: `flutter doctor -v`
2. Check active spec: `cat spec/.current-spec 2>/dev/null || echo "none"`
3. Run code generation if models changed: `flutter pub run build_runner build --delete-conflicting-outputs`

## Commands

```bash
flutter run                   # Run on connected device
flutter run -d chrome         # Web
flutter run -d macos          # macOS
flutter pub get               # Install deps
flutter pub run build_runner build --delete-conflicting-outputs  # Codegen
flutter test                  # Unit tests
flutter test --coverage       # With coverage
flutter build apk             # Android APK
flutter build macos           # macOS app
```

## Tech Stack

- Flutter 3.2+ / Dart 3.2+
- Firebase (Auth, Firestore, Storage)
- Gemini 2.0 for document extraction
- Drift (SQLite) for offline
- Riverpod for state
- GoRouter for navigation
- Material Design 3

## Architecture

```
lib/
  features/
    auth/           # Login, register, password reset
    scanner/        # Camera, gallery, PDF upload
    extraction/     # Gemini AI extraction
    payslips/       # List, detail, sync
  core/
    repositories/   # Data access layer
    services/       # Firebase, Gemini wrappers
    models/         # Drift + Freezed models
```

Clean Architecture + feature-first. Each feature owns its own
`data/`, `domain/`, `presentation/` layers.

## Irish Payroll Fields Extracted

PPS number, employee name, pay period, gross/net pay,
PAYE, PRSI, USC, employer details, tax credits, YTD figures.

## Coding Rules

- No `dynamic` — use proper types
- Every repository has an interface; never depend on concrete implementations
- Use `Either<Failure, T>` (dartz) for all error paths
- Offline-first: local write first, sync second
- Never call Firebase directly from UI — only through repositories

## Code Generation

Drift and Freezed models require codegen. Run after any model change:
```bash
flutter pub run build_runner build --delete-conflicting-outputs
```

## Quality Gate

```bash
flutter analyze && flutter test
```
Both must pass before marking any task done.
