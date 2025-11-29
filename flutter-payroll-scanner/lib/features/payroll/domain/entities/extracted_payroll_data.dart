import 'package:equatable/equatable.dart';

class ExtractedPayrollData extends Equatable {
  const ExtractedPayrollData({
    required this.rawJson,
    required this.rawText,
    required this.confidence,
  });

  final String rawJson;
  final String rawText;
  final double confidence;

  @override
  List<Object?> get props => [rawJson, rawText, confidence];

  ExtractedPayrollData copyWith({
    String? rawJson,
    String? rawText,
    double? confidence,
  }) {
    return ExtractedPayrollData(
      rawJson: rawJson ?? this.rawJson,
      rawText: rawText ?? this.rawText,
      confidence: confidence ?? this.confidence,
    );
  }
}
