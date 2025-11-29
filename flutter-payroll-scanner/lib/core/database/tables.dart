import 'package:drift/drift.dart';

@DataClassName('PayrollDocument')
class PayrollDocuments extends Table {
  TextColumn get id => text()();
  TextColumn get userId => text()();
  TextColumn get fileName => text()();
  TextColumn get filePath => text()();
  TextColumn get fileType => text()(); // 'pdf' or 'image'
  IntColumn get fileSize => integer()();
  TextColumn get status =>
      text()(); // 'pending', 'processing', 'completed', 'error'
  TextColumn get extractedText => text().nullable()();
  TextColumn get firestoreDocId => text().nullable()();
  BoolColumn get isSynced => boolean().withDefault(const Constant(false))();
  DateTimeColumn get createdAt => dateTime()();
  DateTimeColumn get updatedAt => dateTime()();

  @override
  Set<Column> get primaryKey => {id};
}

@DataClassName('PayrollItem')
class PayrollItems extends Table {
  TextColumn get id => text()();
  TextColumn get documentId => text()();
  TextColumn get itemType =>
      text()(); // 'employee_name', 'pps_number', 'gross_pay', 'paye', 'prsi', 'usc', 'net_pay', etc.
  TextColumn get itemLabel => text()();
  TextColumn get itemValue => text()();
  RealColumn get confidence => real().nullable()(); // Gemini confidence score
  IntColumn get position => integer()(); // Position in the document
  TextColumn get metadata => text().nullable()(); // JSON metadata
  DateTimeColumn get createdAt => dateTime()();

  @override
  Set<Column> get primaryKey => {id};
}

@DataClassName('UserProfile')
class UserProfiles extends Table {
  TextColumn get userId => text()();
  TextColumn get email => text()();
  TextColumn get displayName => text().nullable()();
  TextColumn get photoUrl => text().nullable()();
  TextColumn get preferences => text().nullable()(); // JSON preferences
  DateTimeColumn get createdAt => dateTime()();
  DateTimeColumn get updatedAt => dateTime()();

  @override
  Set<Column> get primaryKey => {userId};
}
