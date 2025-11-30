import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:logger/logger.dart';
import 'package:sentry_flutter/sentry_flutter.dart';

import 'app.dart';
import 'core/config/environment.dart';
import 'core/config/firebase_options.dart';
import 'core/services/database_service.dart';
import 'main_dev.dart' show environmentConfigProvider;

final logger = Logger(
  printer: PrettyPrinter(
    methodCount: 0, // No method call stack in production
    errorMethodCount: 8,
    lineLength: 120,
    colors: false,
    printEmojis: false,
    dateTimeFormat: DateTimeFormat.onlyTimeAndSinceStart,
  ),
  level: Level.warning, // Only warnings and errors in production
);

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Production mode - minimal logging
  logger.i('Starting app in PRODUCTION mode');

  // Initialize Sentry for crash reporting
  await SentryFlutter.init(
    (options) {
      // Get DSN from environment variable
      // Set this in your build configuration or CI/CD
      options.dsn = const String.fromEnvironment(
        'SENTRY_DSN',
        defaultValue: '', // Empty = disabled if not configured
      );
      
      // Set environment
      options.environment = 'production';
      
      // Set release version
      options.release = 'payroll-scanner@1.0.0+1';
      
      // Performance monitoring
      options.tracesSampleRate = 0.2; // 20% of transactions
      
      // Attach screenshots on errors (helps debugging)
      options.attachScreenshot = true;
      
      // Attach view hierarchy
      options.attachViewHierarchy = true;
      
      // Send default PII (Personally Identifiable Information)
      options.sendDefaultPii = false; // Don't send PII for privacy
      
      // Enable automatic breadcrumbs
      options.enableAutoSessionTracking = true;
      options.sessionTrackingIntervalMillis = 30000;
      
      // Filter out sensitive data
      options.beforeSend = (event, hint) {
        // Don't send events if DSN is not configured
        if (options.dsn?.isEmpty ?? true) {
          return null;
        }
        
        // Filter out any sensitive data from event
        // You can customize this based on your needs
        return event;
      };
    },
    appRunner: () async {
      await Firebase.initializeApp(
        options: DefaultFirebaseOptions.currentPlatform,
      );

      final databaseService = DatabaseService();
      await databaseService.initialize();

      // Production error handling with Sentry
      FlutterError.onError = (details) {
        logger.e(
          'Flutter error occurred',
          error: details.exception,
          stackTrace: details.stack,
        );
        
        // Send to Sentry
        Sentry.captureException(
          details.exception,
          stackTrace: details.stack,
          hint: Hint.withMap({
            'type': 'FlutterError',
            'library': details.library ?? 'unknown',
          }),
        );
      };

      PlatformDispatcher.instance.onError = (error, stack) {
        logger.e(
          'Platform error occurred',
          error: error,
          stackTrace: stack,
        );
        
        // Send to Sentry
        Sentry.captureException(
          error,
          stackTrace: stack,
          hint: Hint.withMap({'type': 'PlatformError'}),
        );
        
        return true;
      };

      runApp(
        ProviderScope(
          overrides: [
            databaseServiceProvider.overrideWithValue(databaseService),
            environmentConfigProvider
                .overrideWithValue(EnvironmentConfig.production),
          ],
          child: const PayrollScannerApp(),
        ),
      );
    },
  );
}
