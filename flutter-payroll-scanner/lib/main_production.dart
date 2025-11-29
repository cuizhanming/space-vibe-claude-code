import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:logger/logger.dart';

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

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Production mode - minimal logging
  logger.i('Starting app in PRODUCTION mode');

  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );

  final databaseService = DatabaseService();
  await databaseService.initialize();

  // Production error handling - could send to crash reporting service
  FlutterError.onError = (details) {
    logger.e(
      'Flutter error occurred',
      error: details.exception,
      stackTrace: details.stack,
    );
    // TODO: Send to crash reporting service (e.g., Sentry, Crashlytics)
  };

  PlatformDispatcher.instance.onError = (error, stack) {
    logger.e(
      'Platform error occurred',
      error: error,
      stackTrace: stack,
    );
    // TODO: Send to crash reporting service
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
}
