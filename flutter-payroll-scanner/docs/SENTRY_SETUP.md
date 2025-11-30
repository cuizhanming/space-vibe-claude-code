# Sentry Crash Reporting Setup Guide

## Overview

Sentry is now integrated into the production build for comprehensive crash reporting and error tracking.

## Quick Start

### 1. Create Sentry Account

1. Go to [sentry.io](https://sentry.io)
2. Sign up for a free account
3. Create a new project:
   - Platform: **Flutter**
   - Project name: **payroll-scanner**

### 2. Get Your DSN

After creating the project, you'll see your DSN (Data Source Name). It looks like:
```
https://[key]@[organization].ingest.sentry.io/[project-id]
```

### 3. Configure DSN

#### Option A: Environment Variable (Recommended)

```bash
# For local testing
export SENTRY_DSN="your-dsn-here"

# Build with DSN
flutter build apk --dart-define=SENTRY_DSN="your-dsn-here"
```

#### Option B: CI/CD Configuration

**GitHub Actions**:
```yaml
env:
  SENTRY_DSN: ${{ secrets.SENTRY_DSN }}

- name: Build APK
  run: flutter build apk --dart-define=SENTRY_DSN=${{ secrets.SENTRY_DSN }}
```

**GitLab CI**:
```yaml
variables:
  SENTRY_DSN: $SENTRY_DSN

build:
  script:
    - flutter build apk --dart-define=SENTRY_DSN=$SENTRY_DSN
```

### 4. Test Crash Reporting

Add a test button to trigger an error:

```dart
ElevatedButton(
  onPressed: () {
    throw Exception('Test crash for Sentry');
  },
  child: Text('Test Crash'),
)
```

Or use the Sentry service:

```dart
import 'package:payroll_scanner/core/services/sentry_service.dart';

// Capture exception
SentryService.captureException(
  Exception('Something went wrong'),
  hint: 'User tried to upload invalid file',
  extra: {
    'file_size': 1024000,
    'file_type': 'pdf',
  },
);

// Add breadcrumb
SentryService.addBreadcrumb(
  message: 'User clicked upload button',
  category: 'user_action',
  data: {'screen': 'scan_document'},
);
```

## Features Enabled

### ✅ Automatic Error Capture
- Flutter framework errors
- Platform errors (iOS/Android)
- Unhandled exceptions

### ✅ Performance Monitoring
- 20% of transactions tracked
- Automatic performance insights

### ✅ Context Enrichment
- Screenshots on errors
- View hierarchy
- Breadcrumbs (user actions)
- Device information

### ✅ Privacy Protection
- PII (Personally Identifiable Information) not sent by default
- Sensitive data filtering
- User context only includes user ID and email

## User Context Tracking

Set user context after login:

```dart
import 'package:payroll_scanner/core/services/sentry_service.dart';

// After successful login
final user = FirebaseAuth.instance.currentUser;
await SentryService.setUserContext(user);

// On logout
await SentryService.setUserContext(null);
```

## Breadcrumbs for User Flow

Track user actions to understand what led to an error:

```dart
// Navigation
SentryService.addBreadcrumb(
  message: 'Navigated to payroll list',
  category: 'navigation',
);

// User action
SentryService.addBreadcrumb(
  message: 'Uploaded document',
  category: 'user_action',
  data: {'file_name': 'payslip.pdf'},
);

// API call
SentryService.addBreadcrumb(
  message: 'Called Gemini API',
  category: 'http',
  data: {'endpoint': '/api/v1/payroll/extract'},
);
```

## Performance Monitoring

Track slow operations:

```dart
final transaction = SentryService.startTransaction(
  'document_processing',
  'task',
  description: 'Process payroll document',
);

try {
  // Your code here
  await processDocument();
  transaction.status = SpanStatus.ok();
} catch (e) {
  transaction.status = SpanStatus.internalError();
  rethrow;
} finally {
  await transaction.finish();
}
```

## Configuration

Current Sentry configuration in [main_production.dart](file:///Users/cui/Workspace/github/space-vibe-claude-code/flutter-payroll-scanner/lib/main_production.dart):

- **Environment**: `production`
- **Release**: `payroll-scanner@1.0.0+1`
- **Traces Sample Rate**: 20%
- **Screenshots**: Enabled
- **View Hierarchy**: Enabled
- **PII**: Disabled
- **Session Tracking**: Enabled (30s interval)

## Viewing Errors in Sentry

1. Log in to [sentry.io](https://sentry.io)
2. Select your **payroll-scanner** project
3. View:
   - **Issues**: All captured errors
   - **Performance**: Transaction traces
   - **Releases**: Track errors by version
   - **Alerts**: Set up notifications

## Best Practices

### ✅ DO

- Set user context after login
- Add breadcrumbs for important user actions
- Use descriptive error messages
- Include relevant context in extra data
- Test error reporting before production

### ❌ DON'T

- Send PII (passwords, credit cards, etc.)
- Capture every single action as breadcrumb
- Leave test crashes in production code
- Ignore Sentry alerts

## Troubleshooting

### Errors Not Appearing in Sentry

1. **Check DSN is configured**:
   ```bash
   flutter build apk --dart-define=SENTRY_DSN="your-dsn"
   ```

2. **Verify internet connection** on device

3. **Check Sentry dashboard** for project status

4. **Test with manual capture**:
   ```dart
   SentryService.captureMessage('Test message');
   ```

### Too Many Events

Adjust sample rate in `main_production.dart`:
```dart
options.tracesSampleRate = 0.1; // 10% instead of 20%
```

### Sensitive Data in Events

Add filtering in `beforeSend`:
```dart
options.beforeSend = (event, hint) {
  // Remove sensitive data
  event.request?.data?.remove('password');
  return event;
};
```

## Cost Management

Sentry free tier includes:
- 5,000 errors/month
- 10,000 performance units/month
- 1 user

For production apps with more traffic:
- **Team Plan**: $26/month (50K errors)
- **Business Plan**: $80/month (100K errors)

## Integration with Other Tools

### Firebase Crashlytics

If you're also using Firebase Crashlytics:
```dart
// Send to both Sentry and Crashlytics
FlutterError.onError = (details) {
  Sentry.captureException(details.exception, stackTrace: details.stack);
  FirebaseCrashlytics.instance.recordFlutterError(details);
};
```

### Slack Notifications

Configure in Sentry dashboard:
1. Settings → Integrations → Slack
2. Connect workspace
3. Set up alert rules

## Next Steps

1. ✅ Get Sentry DSN from sentry.io
2. ✅ Configure DSN in build process
3. ✅ Test error reporting
4. ✅ Set up user context tracking
5. ✅ Configure Slack/email alerts
6. ✅ Monitor error trends

## Support

- **Sentry Docs**: https://docs.sentry.io/platforms/flutter/
- **Sentry Discord**: https://discord.gg/sentry
- **GitHub Issues**: https://github.com/getsentry/sentry-dart
