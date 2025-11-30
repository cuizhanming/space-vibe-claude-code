import 'package:flutter_test/flutter_test.dart';
import 'package:payroll_scanner/features/payroll/domain/entities/payroll_document_entity.dart';

void main() {
  group('PayrollDocumentEntity', () {
    final testCreatedAt = DateTime(2024, 1, 1);
    final testUpdatedAt = DateTime(2024, 1, 2);

    PayrollDocumentEntity createTestEntity() {
      return PayrollDocumentEntity(
        id: 'test-id-123',
        userId: 'user-456',
        fileName: 'payslip_jan_2024.pdf',
        filePath: '/path/to/file.pdf',
        fileType: 'pdf',
        fileSize: 102400,
        status: 'completed',
        extractedText: 'Sample extracted text',
        firestoreDocId: 'firestore-doc-789',
        isSynced: true,
        createdAt: testCreatedAt,
        updatedAt: testUpdatedAt,
      );
    }

    test('should create instance with all fields', () {
      // Act
      final entity = createTestEntity();

      // Assert
      expect(entity.id, 'test-id-123');
      expect(entity.userId, 'user-456');
      expect(entity.fileName, 'payslip_jan_2024.pdf');
      expect(entity.filePath, '/path/to/file.pdf');
      expect(entity.fileType, 'pdf');
      expect(entity.fileSize, 102400);
      expect(entity.status, 'completed');
      expect(entity.extractedText, 'Sample extracted text');
      expect(entity.firestoreDocId, 'firestore-doc-789');
      expect(entity.isSynced, true);
      expect(entity.createdAt, testCreatedAt);
      expect(entity.updatedAt, testUpdatedAt);
    });

    test('should create instance with nullable fields as null', () {
      // Act
      final entity = PayrollDocumentEntity(
        id: 'test-id',
        userId: 'user-id',
        fileName: 'test.pdf',
        filePath: '/path',
        fileType: 'pdf',
        fileSize: 1024,
        status: 'pending',
        createdAt: testCreatedAt,
        updatedAt: testUpdatedAt,
      );

      // Assert
      expect(entity.extractedText, isNull);
      expect(entity.firestoreDocId, isNull);
      expect(entity.isSynced, false); // default value
    });

    test('should support equality comparison', () {
      // Arrange
      final entity1 = createTestEntity();
      final entity2 = createTestEntity();
      final entity3 = createTestEntity().copyWith(id: 'different-id');

      // Assert
      expect(entity1, equals(entity2));
      expect(entity1, isNot(equals(entity3)));
    });

    test('should create copy with modified fields', () {
      // Arrange
      final original = createTestEntity();

      // Act
      final modified = original.copyWith(
        status: 'processing',
        isSynced: false,
      );

      // Assert
      expect(modified.id, original.id);
      expect(modified.status, 'processing');
      expect(modified.isSynced, false);
      expect(modified, isNot(equals(original)));
    });

    test('should handle different file types', () {
      // Arrange & Act
      final pdfDoc = createTestEntity();
      final imageDoc = pdfDoc.copyWith(
        fileType: 'image',
        fileName: 'payslip.jpg',
      );

      // Assert
      expect(pdfDoc.fileType, 'pdf');
      expect(imageDoc.fileType, 'image');
    });

    test('should handle different status values', () {
      // Arrange
      final baseDoc = createTestEntity();

      // Act & Assert
      final pending = baseDoc.copyWith(status: 'pending');
      expect(pending.status, 'pending');

      final processing = baseDoc.copyWith(status: 'processing');
      expect(processing.status, 'processing');

      final completed = baseDoc.copyWith(status: 'completed');
      expect(completed.status, 'completed');

      final error = baseDoc.copyWith(status: 'error');
      expect(error.status, 'error');
    });

    test('should maintain all fields in props for equality', () {
      // Arrange
      final entity = createTestEntity();

      // Assert
      expect(entity.props.length, 12);
      expect(entity.props, contains(entity.id));
      expect(entity.props, contains(entity.userId));
      expect(entity.props, contains(entity.fileName));
      expect(entity.props, contains(entity.status));
    });
  });
}
