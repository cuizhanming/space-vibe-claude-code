import 'package:equatable/equatable.dart';

class PayrollDocumentEntity extends Equatable {
  const PayrollDocumentEntity({
    required this.id,
    required this.userId,
    required this.fileName,
    required this.filePath,
    required this.fileType,
    required this.fileSize,
    required this.status,
    this.extractedText,
    this.firestoreDocId,
    this.isSynced = false,
    required this.createdAt,
    required this.updatedAt,
  });

  final String id;
  final String userId;
  final String fileName;
  final String filePath;
  final String fileType; // 'pdf' or 'image'
  final int fileSize;
  final String status; // 'pending', 'processing', 'completed', 'error'
  final String? extractedText;
  final String? firestoreDocId;
  final bool isSynced;
  final DateTime createdAt;
  final DateTime updatedAt;

  @override
  List<Object?> get props => [
        id,
        userId,
        fileName,
        filePath,
        fileType,
        fileSize,
        status,
        extractedText,
        firestoreDocId,
        isSynced,
        createdAt,
        updatedAt,
      ];

  PayrollDocumentEntity copyWith({
    String? id,
    String? userId,
    String? fileName,
    String? filePath,
    String? fileType,
    int? fileSize,
    String? status,
    String? extractedText,
    String? firestoreDocId,
    bool? isSynced,
    DateTime? createdAt,
    DateTime? updatedAt,
  }) {
    return PayrollDocumentEntity(
      id: id ?? this.id,
      userId: userId ?? this.userId,
      fileName: fileName ?? this.fileName,
      filePath: filePath ?? this.filePath,
      fileType: fileType ?? this.fileType,
      fileSize: fileSize ?? this.fileSize,
      status: status ?? this.status,
      extractedText: extractedText ?? this.extractedText,
      firestoreDocId: firestoreDocId ?? this.firestoreDocId,
      isSynced: isSynced ?? this.isSynced,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
    );
  }
}
