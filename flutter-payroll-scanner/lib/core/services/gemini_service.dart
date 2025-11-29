import 'dart:io';
import 'dart:typed_data';

import 'package:dartz/dartz.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:google_generative_ai/google_generative_ai.dart';

import '../errors/failures.dart';
import '../../features/payroll/domain/entities/extracted_payroll_data.dart';

class GeminiService {
  GeminiService({required String apiKey})
      : _model = GenerativeModel(
          model: 'gemini-2.0-flash-exp',
          apiKey: apiKey,
          generationConfig: GenerationConfig(
            temperature: 0.1,
            topK: 32,
            topP: 0.95,
            maxOutputTokens: 8192,
          ),
        );

  final GenerativeModel _model;

  static const String _irishPayrollPrompt = '''
You are an expert at extracting information from Irish payroll documents.

Analyze the provided image and extract all payroll information following Irish payroll standards.

Extract the following fields if present:
- Employee Name
- PPS Number (Personal Public Service Number)
- Pay Period (start and end dates)
- Payment Date
- Gross Pay
- PAYE (Pay As You Earn tax)
- PRSI (Pay Related Social Insurance)
- USC (Universal Social Charge)
- Other Deductions (itemize if possible)
- Net Pay
- Employer Name
- Employer Registration Number
- Tax Credits Applied
- Year to Date (YTD) figures for: Gross Pay, PAYE, PRSI, USC, Net Pay

Return the data in the following JSON format:
{
  "employee_name": "string",
  "pps_number": "string",
  "pay_period_start": "YYYY-MM-DD",
  "pay_period_end": "YYYY-MM-DD",
  "payment_date": "YYYY-MM-DD",
  "gross_pay": "number",
  "paye": "number",
  "prsi": "number",
  "usc": "number",
  "other_deductions": [
    {"description": "string", "amount": "number"}
  ],
  "net_pay": "number",
  "employer_name": "string",
  "employer_registration_number": "string",
  "tax_credits": "number",
  "ytd_gross_pay": "number",
  "ytd_paye": "number",
  "ytd_prsi": "number",
  "ytd_usc": "number",
  "ytd_net_pay": "number",
  "additional_info": {
    "key": "value"
  }
}

If a field is not found, use null. Be as accurate as possible.
Return ONLY valid JSON, no additional text.
''';

  Future<Either<Failure, ExtractedPayrollData>> extractFromImage(
    File imageFile,
  ) async {
    try {
      final imageBytes = await imageFile.readAsBytes();
      return _processImage(imageBytes);
    } catch (e) {
      return Left(GeminiFailure('Failed to read image file: $e'));
    }
  }

  Future<Either<Failure, ExtractedPayrollData>> extractFromImageBytes(
    Uint8List imageBytes,
  ) async {
    return _processImage(imageBytes);
  }

  Future<Either<Failure, ExtractedPayrollData>> _processImage(
    Uint8List imageBytes,
  ) async {
    try {
      final content = [
        Content.multi([
          TextPart(_irishPayrollPrompt),
          DataPart('image/jpeg', imageBytes),
        ]),
      ];

      final response = await _model.generateContent(content);

      if (response.text == null || response.text!.isEmpty) {
        return const Left(
          GeminiFailure('No response received from Gemini API'),
        );
      }

      // Extract JSON from response (handle potential markdown formatting)
      String jsonText = response.text!.trim();

      // Remove markdown code blocks if present
      if (jsonText.startsWith('```json')) {
        jsonText = jsonText
            .replaceFirst('```json', '')
            .replaceFirst(RegExp(r'```$'), '')
            .trim();
      } else if (jsonText.startsWith('```')) {
        jsonText = jsonText
            .replaceFirst('```', '')
            .replaceFirst(RegExp(r'```$'), '')
            .trim();
      }

      return Right(
        ExtractedPayrollData(
          rawJson: jsonText,
          rawText: response.text!,
          confidence: _calculateConfidence(response),
        ),
      );
    } on GenerativeAIException catch (e) {
      return Left(GeminiFailure('Gemini API error: ${e.message}'));
    } catch (e) {
      return Left(GeminiFailure('Failed to extract payroll data: $e'));
    }
  }

  Future<Either<Failure, String>> extractTextFromPdf(File pdfFile) async {
    // For PDF processing, we would need to convert PDF pages to images first
    // This is a simplified version that returns an error message
    // In a real implementation, you would use a PDF to image converter
    return const Left(
      GeminiFailure(
        'PDF processing requires conversion to images. '
        'Please use a PDF rendering library to convert pages to images first.',
      ),
    );
  }

  double _calculateConfidence(GenerateContentResponse response) {
    // This is a simplified confidence calculation
    // In a real implementation, you might want to use safety ratings
    // or other metrics from the response
    if (response.text != null && response.text!.isNotEmpty) {
      return 0.85; // High confidence if we got a response
    }
    return 0.0;
  }

  // Validate Irish PPS Number format
  static bool validatePpsNumber(String ppsNumber) {
    // Irish PPS Number format: 7 digits followed by 1 or 2 letters
    final regex = RegExp(r'^\d{7}[A-Z]{1,2}$');
    return regex.hasMatch(ppsNumber.toUpperCase().replaceAll(' ', ''));
  }

  // Format currency for Irish locale
  static String formatCurrency(double amount) {
    return '€${amount.toStringAsFixed(2)}';
  }
}

// Provider for Gemini Service
// Note: API key should be stored securely (e.g., in environment variables)
final geminiServiceProvider = Provider<GeminiService>((ref) {
  // TODO: Replace with your actual Gemini API key from environment or secure storage
  const apiKey = String.fromEnvironment(
    'GEMINI_API_KEY',
    defaultValue: 'YOUR_GEMINI_API_KEY_HERE',
  );

  return GeminiService(apiKey: apiKey);
});
