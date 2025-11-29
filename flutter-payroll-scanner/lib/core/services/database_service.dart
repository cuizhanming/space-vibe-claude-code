import 'dart:io';

import 'package:drift/drift.dart';
import 'package:drift/native.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:path/path.dart' as p;
import 'package:path_provider/path_provider.dart';

import '../database/tables.dart';

part 'database_service.g.dart';

@DriftDatabase(tables: [PayrollDocuments, PayrollItems, UserProfiles])
class DatabaseService extends _$DatabaseService {
  DatabaseService() : super(_openConnection());

  @override
  int get schemaVersion => 1;

  Future<void> initialize() async {
    // Database will be initialized when first accessed
    await customSelect('SELECT 1').get();
  }

  // PayrollDocument operations
  Future<List<PayrollDocument>> getAllPayrollDocuments(String userId) {
    return (select(payrollDocuments)
          ..where((tbl) => tbl.userId.equals(userId))
          ..orderBy([
            (tbl) => OrderingTerm(
                  expression: tbl.createdAt,
                  mode: OrderingMode.desc,
                ),
          ]))
        .get();
  }

  Future<PayrollDocument?> getPayrollDocumentById(String id) {
    return (select(payrollDocuments)..where((tbl) => tbl.id.equals(id)))
        .getSingleOrNull();
  }

  Future<int> insertPayrollDocument(PayrollDocumentsCompanion document) {
    return into(payrollDocuments).insert(document);
  }

  Future<bool> updatePayrollDocument(PayrollDocument document) {
    return update(payrollDocuments).replace(document);
  }

  Future<int> deletePayrollDocument(String id) {
    return (delete(payrollDocuments)..where((tbl) => tbl.id.equals(id))).go();
  }

  // PayrollItem operations
  Future<List<PayrollItem>> getPayrollItemsByDocumentId(String documentId) {
    return (select(payrollItems)
          ..where((tbl) => tbl.documentId.equals(documentId)))
        .get();
  }

  Future<int> insertPayrollItem(PayrollItemsCompanion item) {
    return into(payrollItems).insert(item);
  }

  Future<int> insertPayrollItems(List<PayrollItemsCompanion> items) async {
    return await batch((batch) {
      batch.insertAll(payrollItems, items);
    });
  }

  Future<bool> updatePayrollItem(PayrollItem item) {
    return update(payrollItems).replace(item);
  }

  Future<int> deletePayrollItemsByDocumentId(String documentId) {
    return (delete(payrollItems)
          ..where((tbl) => tbl.documentId.equals(documentId)))
        .go();
  }

  // UserProfile operations
  Future<UserProfile?> getUserProfile(String userId) {
    return (select(userProfiles)..where((tbl) => tbl.userId.equals(userId)))
        .getSingleOrNull();
  }

  Future<int> insertUserProfile(UserProfilesCompanion profile) {
    return into(userProfiles).insert(profile);
  }

  Future<bool> updateUserProfile(UserProfile profile) {
    return update(userProfiles).replace(profile);
  }

  @override
  MigrationStrategy get migration {
    return MigrationStrategy(
      onCreate: (Migrator m) async {
        await m.createAll();
      },
      onUpgrade: (Migrator m, int from, int to) async {
        // Add migration logic here when schema changes
      },
    );
  }
}

LazyDatabase _openConnection() {
  return LazyDatabase(() async {
    final dbFolder = await getApplicationDocumentsDirectory();
    final file = File(p.join(dbFolder.path, 'payroll_scanner.db'));
    return NativeDatabase(file);
  });
}

final databaseServiceProvider = Provider<DatabaseService>((ref) {
  throw UnimplementedError('DatabaseService must be overridden in main.dart');
});
