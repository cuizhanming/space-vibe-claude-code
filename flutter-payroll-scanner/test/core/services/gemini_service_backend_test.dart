import 'package:flutter_test/flutter_test.dart';
import 'package:payroll_scanner/core/services/gemini_service_backend.dart';

void main() {
  group('GeminiServiceBackend Utility Methods', () {
    group('validatePpsNumber', () {
      test('should validate correct PPS number format', () {
        // Valid formats: 7 digits + 1-2 uppercase letters
        expect(GeminiServiceBackend.validatePpsNumber('1234567A'), true);
        expect(GeminiServiceBackend.validatePpsNumber('1234567AB'), true);
        expect(GeminiServiceBackend.validatePpsNumber('9876543Z'), true);
        expect(GeminiServiceBackend.validatePpsNumber('1111111XY'), true);
      });

      test('should handle lowercase letters', () {
        // Should convert to uppercase internally
        expect(GeminiServiceBackend.validatePpsNumber('1234567a'), true);
        expect(GeminiServiceBackend.validatePpsNumber('1234567ab'), true);
      });

      test('should handle spaces in PPS number', () {
        // Should remove spaces internally
        expect(GeminiServiceBackend.validatePpsNumber('1234567 A'), true);
        expect(GeminiServiceBackend.validatePpsNumber('1234567 AB'), true);
        expect(GeminiServiceBackend.validatePpsNumber(' 1234567A '), true);
      });

      test('should reject invalid formats', () {
        // Too few digits
        expect(GeminiServiceBackend.validatePpsNumber('123456A'), false);
        
        // Too many digits
        expect(GeminiServiceBackend.validatePpsNumber('12345678A'), false);
        
        // No letters
        expect(GeminiServiceBackend.validatePpsNumber('1234567'), false);
        
        // Too many letters
        expect(GeminiServiceBackend.validatePpsNumber('1234567ABC'), false);
        
        // Numbers in letter position
        expect(GeminiServiceBackend.validatePpsNumber('12345671'), false);
        
        // Letters in number position
        expect(GeminiServiceBackend.validatePpsNumber('A234567A'), false);
        
        // Empty string
        expect(GeminiServiceBackend.validatePpsNumber(''), false);
        
        // Special characters
        expect(GeminiServiceBackend.validatePpsNumber('1234567-A'), false);
      });
    });

    group('formatCurrency', () {
      test('should format positive amounts correctly', () {
        expect(GeminiServiceBackend.formatCurrency(100.0), '€100.00');
        expect(GeminiServiceBackend.formatCurrency(1234.56), '€1234.56');
        expect(GeminiServiceBackend.formatCurrency(0.99), '€0.99');
      });

      test('should format zero correctly', () {
        expect(GeminiServiceBackend.formatCurrency(0.0), '€0.00');
      });

      test('should format negative amounts correctly', () {
        expect(GeminiServiceBackend.formatCurrency(-50.0), '€-50.00');
        expect(GeminiServiceBackend.formatCurrency(-1234.56), '€-1234.56');
      });

      test('should always show two decimal places', () {
        expect(GeminiServiceBackend.formatCurrency(100), '€100.00');
        expect(GeminiServiceBackend.formatCurrency(100.1), '€100.10');
        expect(GeminiServiceBackend.formatCurrency(100.123), '€100.12');
      });

      test('should handle large amounts', () {
        expect(
          GeminiServiceBackend.formatCurrency(1000000.0),
          '€1000000.00',
        );
        expect(
          GeminiServiceBackend.formatCurrency(999999.99),
          '€999999.99',
        );
      });

      test('should handle very small amounts', () {
        expect(GeminiServiceBackend.formatCurrency(0.01), '€0.01');
        expect(GeminiServiceBackend.formatCurrency(0.001), '€0.00');
      });
    });
  });
}
