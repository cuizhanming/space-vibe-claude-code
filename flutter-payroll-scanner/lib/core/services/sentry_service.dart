import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:sentry_flutter/sentry_flutter.dart';

/// Service for managing Sentry error reporting and user context
class SentryService {
  /// Set user context for error tracking
  static Future<void> setUserContext(User? user) async {
    if (user != null) {
      await Sentry.configureScope((scope) {
        scope.setUser(SentryUser(
          id: user.uid,
          email: user.email,
          // Don't send other PII unless necessary
        ));
      });
    } else {
      // Clear user context on logout
      await Sentry.configureScope((scope) {
        scope.setUser(null);
      });
    }
  }

  /// Manually capture an exception with additional context
  static Future<SentryId> captureException(
    dynamic exception, {
    dynamic stackTrace,
    String? hint,
    Map<String, dynamic>? extra,
  }) async {
    return await Sentry.captureException(
      exception,
      stackTrace: stackTrace,
      hint: hint != null ? Hint.withMap({'message': hint}) : null,
      withScope: (scope) {
        if (extra != null) {
          extra.forEach((key, value) {
            scope.setExtra(key, value);
          });
        }
      },
    );
  }

  /// Add breadcrumb for tracking user actions
  static void addBreadcrumb({
    required String message,
    String? category,
    Map<String, dynamic>? data,
    SentryLevel level = SentryLevel.info,
  }) {
    Sentry.addBreadcrumb(
      Breadcrumb(
        message: message,
        category: category,
        data: data,
        level: level,
        timestamp: DateTime.now(),
      ),
    );
  }

  /// Capture a custom message
  static Future<SentryId> captureMessage(
    String message, {
    SentryLevel level = SentryLevel.info,
    Map<String, dynamic>? extra,
  }) async {
    return await Sentry.captureMessage(
      message,
      level: level,
      withScope: (scope) {
        if (extra != null) {
          extra.forEach((key, value) {
            scope.setExtra(key, value);
          });
        }
      },
    );
  }

  /// Start a transaction for performance monitoring
  static ISentrySpan startTransaction(
    String name,
    String operation, {
    String? description,
  }) {
    return Sentry.startTransaction(
      name,
      operation,
      description: description,
    );
  }
}

/// Provider for Sentry service
final sentryServiceProvider = Provider<SentryService>((ref) {
  return SentryService();
});
