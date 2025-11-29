import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:logger/logger.dart';

import 'app.dart';
import 'core/config/environment.dart';
import 'core/config/firebase_options.dart';
import 'core/services/database_service.dart';

final logger = Logger(
  printer: PrettyPrinter(
    methodCount: 2,
    errorMethodCount: 8,
    lineLength: 120,
    colors: true,
    printEmojis: true,
    dateTimeFormat: DateTimeFormat.onlyTimeAndSinceStart,
  ),
  level: Level.debug, // Verbose logging for development
);

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Log environment info
  logger.i('Starting app in DEVELOPMENT mode');
  logger.i(EnvironmentConfig.development.toString());

  // Initialize Firebase
  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );

  // Initialize local database
  final databaseService = DatabaseService();
  await databaseService.initialize();

  // Configure error handling
  FlutterError.onError = (details) {
    logger.e(
      'Flutter error occurred',
      error: details.exception,
      stackTrace: details.stack,
    );
  };

  PlatformDispatcher.instance.onError = (error, stack) {
    logger.e(
      'Platform error occurred',
      error: error,
      stackTrace: stack,
    );
    return true;
  };

  runApp(
    ProviderScope(
      overrides: [
        databaseServiceProvider.overrideWithValue(databaseService),
        environmentConfigProvider
            .overrideWithValue(EnvironmentConfig.development),
      ],
      child: const PayrollScannerApp(),
    ),
  );
}

// Environment config provider
final environmentConfigProvider = Provider<EnvironmentConfig>((ref) {
  throw UnimplementedError('Environment config must be overridden');
});
