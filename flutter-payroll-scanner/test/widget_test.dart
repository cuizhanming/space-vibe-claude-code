import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:payroll_scanner/app.dart';
import 'package:payroll_scanner/core/services/database_service.dart';

void main() {
  testWidgets('PayrollScannerApp renders without crashing',
      (WidgetTester tester) async {
    // Create a mock database service
    final mockDatabaseService = DatabaseService();

    // Build our app and trigger a frame
    await tester.pumpWidget(
      ProviderScope(
        overrides: [
          databaseServiceProvider.overrideWithValue(mockDatabaseService),
        ],
        child: const PayrollScannerApp(),
      ),
    );

    // Wait for the app to settle
    await tester.pumpAndSettle();

    // Verify that the app renders (should show login or home page)
    // The app should not crash during initialization
    expect(find.byType(MaterialApp), findsOneWidget);
  });

  testWidgets('App uses correct theme configuration',
      (WidgetTester tester) async {
    final mockDatabaseService = DatabaseService();

    await tester.pumpWidget(
      ProviderScope(
        overrides: [
          databaseServiceProvider.overrideWithValue(mockDatabaseService),
        ],
        child: const PayrollScannerApp(),
      ),
    );

    await tester.pumpAndSettle();

    // Verify MaterialApp is configured
    final materialApp = tester.widget<MaterialApp>(find.byType(MaterialApp));
    expect(materialApp.title, 'Payroll Scanner');
    expect(materialApp.debugShowCheckedModeBanner, false);
  });
}
