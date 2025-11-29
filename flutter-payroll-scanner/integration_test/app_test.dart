import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:payroll_scanner/main.dart' as app;

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  group('Payroll Scanner App Integration Tests', () {
    testWidgets('Complete user flow test', (WidgetTester tester) async {
      // Start the app
      app.main();
      await tester.pumpAndSettle();

      // Test 1: Should show login page
      expect(find.text('Login'), findsOneWidget);
      print('✓ Login page loaded');

      // Test 2: Navigate to register page
      final registerButton = find.text('Sign Up');
      if (registerButton.evaluate().isNotEmpty) {
        await tester.tap(registerButton);
        await tester.pumpAndSettle();
        print('✓ Navigated to registration page');

        // Test 3: Fill registration form
        await tester.enterText(find.byKey(const Key('email_field')), 'test@example.com');
        await tester.enterText(find.byKey(const Key('password_field')), 'Test123456');
        print('✓ Filled registration form');

        // Test 4: Submit registration
        final submitButton = find.text('Register');
        await tester.tap(submitButton);
        await tester.pumpAndSettle(const Duration(seconds: 5));
        print('✓ Submitted registration');

        // Test 5: Should navigate to home page
        expect(find.text('Home'), findsOneWidget);
        print('✓ Successfully logged in and reached home page');
      }
    });
  });
}
