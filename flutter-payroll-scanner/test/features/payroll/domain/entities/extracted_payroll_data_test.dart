import 'package:flutter_test/flutter_test.dart';
import 'package:payroll_scanner/features/payroll/domain/entities/extracted_payroll_data.dart';

void main() {
  group('ExtractedPayrollData', () {
    const testRawJson = '{"employee_name": "John Doe"}';
    const testRawText = 'Employee: John Doe';
    const testConfidence = 0.85;

    test('should create instance with required fields', () {
      // Arrange & Act
      const data = ExtractedPayrollData(
        rawJson: testRawJson,
        rawText: testRawText,
        confidence: testConfidence,
      );

      // Assert
      expect(data.rawJson, testRawJson);
      expect(data.rawText, testRawText);
      expect(data.confidence, testConfidence);
    });

    test('should support equality comparison', () {
      // Arrange
      const data1 = ExtractedPayrollData(
        rawJson: testRawJson,
        rawText: testRawText,
        confidence: testConfidence,
      );

      const data2 = ExtractedPayrollData(
        rawJson: testRawJson,
        rawText: testRawText,
        confidence: testConfidence,
      );

      const data3 = ExtractedPayrollData(
        rawJson: 'different',
        rawText: testRawText,
        confidence: testConfidence,
      );

      // Assert
      expect(data1, equals(data2));
      expect(data1, isNot(equals(data3)));
    });

    test('should create copy with modified fields', () {
      // Arrange
      const original = ExtractedPayrollData(
        rawJson: testRawJson,
        rawText: testRawText,
        confidence: testConfidence,
      );

      // Act
      final modified = original.copyWith(
        confidence: 0.95,
      );

      // Assert
      expect(modified.rawJson, original.rawJson);
      expect(modified.rawText, original.rawText);
      expect(modified.confidence, 0.95);
      expect(modified, isNot(equals(original)));
    });

    test('should maintain original values when copyWith called with nulls', () {
      // Arrange
      const original = ExtractedPayrollData(
        rawJson: testRawJson,
        rawText: testRawText,
        confidence: testConfidence,
      );

      // Act
      final copy = original.copyWith();

      // Assert
      expect(copy, equals(original));
    });

    test('should have correct props for equality', () {
      // Arrange
      const data = ExtractedPayrollData(
        rawJson: testRawJson,
        rawText: testRawText,
        confidence: testConfidence,
      );

      // Assert
      expect(data.props, [testRawJson, testRawText, testConfidence]);
    });
  });
}
