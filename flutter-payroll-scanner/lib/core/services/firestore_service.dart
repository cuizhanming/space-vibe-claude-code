import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:dartz/dartz.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../errors/failures.dart';
import '../../features/payroll/domain/entities/payroll_document_entity.dart';

class FirestoreService {
  FirestoreService(this._firestore);

  final FirebaseFirestore _firestore;

  static const String _payrollDocumentsCollection = 'payroll_documents';
  static const String _payrollItemsCollection = 'payroll_items';

  // Save payroll document to Firestore
  Future<Either<Failure, String>> savePayrollDocument({
    required String userId,
    required PayrollDocumentEntity document,
  }) async {
    try {
      final docRef = await _firestore
          .collection(_payrollDocumentsCollection)
          .add({
        'userId': userId,
        'fileName': document.fileName,
        'filePath': document.filePath,
        'fileType': document.fileType,
        'fileSize': document.fileSize,
        'status': document.status,
        'extractedText': document.extractedText,
        'createdAt': FieldValue.serverTimestamp(),
        'updatedAt': FieldValue.serverTimestamp(),
      });

      return Right(docRef.id);
    } catch (e) {
      return Left(NetworkFailure('Failed to save document: $e'));
    }
  }

  // Get all payroll documents for a user
  Future<Either<Failure, List<Map<String, dynamic>>>> getPayrollDocuments({
    required String userId,
  }) async {
    try {
      final querySnapshot = await _firestore
          .collection(_payrollDocumentsCollection)
          .where('userId', isEqualTo: userId)
          .orderBy('createdAt', descending: true)
          .get();

      final documents = querySnapshot.docs
          .map((doc) => {
                'id': doc.id,
                ...doc.data(),
              })
          .toList();

      return Right(documents);
    } catch (e) {
      return Left(NetworkFailure('Failed to fetch documents: $e'));
    }
  }

  // Get a single payroll document
  Future<Either<Failure, Map<String, dynamic>>> getPayrollDocument({
    required String documentId,
  }) async {
    try {
      final docSnapshot = await _firestore
          .collection(_payrollDocumentsCollection)
          .doc(documentId)
          .get();

      if (!docSnapshot.exists) {
        return const Left(NetworkFailure('Document not found'));
      }

      return Right({
        'id': docSnapshot.id,
        ...docSnapshot.data()!,
      });
    } catch (e) {
      return Left(NetworkFailure('Failed to fetch document: $e'));
    }
  }

  // Update payroll document
  Future<Either<Failure, void>> updatePayrollDocument({
    required String documentId,
    required Map<String, dynamic> updates,
  }) async {
    try {
      await _firestore
          .collection(_payrollDocumentsCollection)
          .doc(documentId)
          .update({
        ...updates,
        'updatedAt': FieldValue.serverTimestamp(),
      });

      return const Right(null);
    } catch (e) {
      return Left(NetworkFailure('Failed to update document: $e'));
    }
  }

  // Delete payroll document
  Future<Either<Failure, void>> deletePayrollDocument({
    required String documentId,
  }) async {
    try {
      // Delete all items associated with this document
      final itemsQuery = await _firestore
          .collection(_payrollItemsCollection)
          .where('documentId', isEqualTo: documentId)
          .get();

      final batch = _firestore.batch();

      for (final doc in itemsQuery.docs) {
        batch.delete(doc.reference);
      }

      // Delete the document itself
      batch.delete(
        _firestore.collection(_payrollDocumentsCollection).doc(documentId),
      );

      await batch.commit();

      return const Right(null);
    } catch (e) {
      return Left(NetworkFailure('Failed to delete document: $e'));
    }
  }

  // Save payroll items to Firestore
  Future<Either<Failure, void>> savePayrollItems({
    required String documentId,
    required List<Map<String, dynamic>> items,
  }) async {
    try {
      final batch = _firestore.batch();

      for (final item in items) {
        final docRef = _firestore.collection(_payrollItemsCollection).doc();
        batch.set(docRef, {
          'documentId': documentId,
          'itemType': item['itemType'],
          'itemLabel': item['itemLabel'],
          'itemValue': item['itemValue'],
          'confidence': item['confidence'],
          'position': item['position'],
          'metadata': item['metadata'],
          'createdAt': FieldValue.serverTimestamp(),
        });
      }

      await batch.commit();
      return const Right(null);
    } catch (e) {
      return Left(NetworkFailure('Failed to save items: $e'));
    }
  }

  // Get payroll items for a document
  Future<Either<Failure, List<Map<String, dynamic>>>> getPayrollItems({
    required String documentId,
  }) async {
    try {
      final querySnapshot = await _firestore
          .collection(_payrollItemsCollection)
          .where('documentId', isEqualTo: documentId)
          .orderBy('position')
          .get();

      final items = querySnapshot.docs
          .map((doc) => {
                'id': doc.id,
                ...doc.data(),
              })
          .toList();

      return Right(items);
    } catch (e) {
      return Left(NetworkFailure('Failed to fetch items: $e'));
    }
  }

  // Stream payroll documents for real-time updates
  Stream<List<Map<String, dynamic>>> streamPayrollDocuments({
    required String userId,
  }) {
    return _firestore
        .collection(_payrollDocumentsCollection)
        .where('userId', isEqualTo: userId)
        .orderBy('createdAt', descending: true)
        .snapshots()
        .map((snapshot) => snapshot.docs
            .map((doc) => {
                  'id': doc.id,
                  ...doc.data(),
                })
            .toList());
  }
}

final firestoreServiceProvider = Provider<FirestoreService>((ref) {
  return FirestoreService(FirebaseFirestore.instance);
});
