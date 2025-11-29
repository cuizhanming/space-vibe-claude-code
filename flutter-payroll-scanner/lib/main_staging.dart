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
    methodCount: 2,
    errorMethodCount: 8,
    lineLength: 120,
    colors: true,
    printEmojis: true,
    dateTimeFormat: DateTimeFormat.onlyTimeAndSinceStart,
  ),
  level: Level.info, // Less verbose for staging
);

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  logger.i('Starting app in STAGING mode');
  logger.i(EnvironmentConfig.staging.toString());

  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );

  final databaseService = DatabaseService();
  await databaseService.initialize();

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
        environmentConfigProvider.overrideWithValue(EnvironmentConfig.staging),
      ],
      child: const PayrollScannerApp(),
    ),
  );
}
