import 'dart:convert';
import 'dart:io';
import 'dart:typed_data';

import 'package:dartz/dartz.dart';
import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../config/environment.dart';
import '../errors/failures.dart';
import '../../features/payroll/domain/entities/extracted_payroll_data.dart';

/// Production-ready Gemini service that uses backend proxy
/// This approach keeps API keys secure and enables:
/// - Rate limiting
/// - Cost control
/// - Usage monitoring
/// - User authentication
/// - Response caching
class GeminiServiceBackend {
  GeminiServiceBackend({
    required Dio dio,
    required EnvironmentConfig config,
  })  : _dio = dio,
        _config = config;

  final Dio _dio;
  final EnvironmentConfig _config;

  /// Extract payroll data from image using backend proxy
  Future<Either<Failure, ExtractedPayrollData>> extractFromImage(
    File imageFile,
  ) async {
    try {
      // Get current user's authentication token
      final user = FirebaseAuth.instance.currentUser;
      if (user == null) {
        return const Left(AuthFailure('User not authenticated'));
      }

      final token = await user.getIdToken();

      // Convert image to base64
      final bytes = await imageFile.readAsBytes();
      final base64Image = base64Encode(bytes);

      // Call backend proxy endpoint
      final response = await _dio.post(
        '${_config.backendUrl}/api/v1/payroll/extract',
        data: {
          'imageBase64': base64Image,
          'mimeType': _getMimeType(imageFile.path),
        },
        options: Options(
          headers: {
            'Authorization': 'Bearer $token',
            'Content-Type': 'application/json',
          },
          sendTimeout: _config.apiTimeout,
          receiveTimeout: _config.apiTimeout,
        ),
      );

      if (response.statusCode == 200) {
        final data = response.data as Map<String, dynamic>;

        return Right(
          ExtractedPayrollData(
            rawJson: data['extractedData'] as String,
            rawText: data['rawText'] as String,
            confidence: (data['confidence'] as num?)?.toDouble() ?? 0.0,
          ),
        );
      } else {
        return Left(
          NetworkFailure('Server returned ${response.statusCode}'),
        );
      }
    } on DioException catch (e) {
      return Left(_handleDioError(e));
    } on FirebaseAuthException catch (e) {
      return Left(AuthFailure('Authentication error: ${e.message}'));
    } catch (e) {
      return Left(GeminiFailure('Extraction failed: $e'));
    }
  }

  /// Extract payroll data from image bytes
  Future<Either<Failure, ExtractedPayrollData>> extractFromImageBytes(
    Uint8List imageBytes, {
    String mimeType = 'image/jpeg',
  }) async {
    try {
      final user = FirebaseAuth.instance.currentUser;
      if (user == null) {
        return const Left(AuthFailure('User not authenticated'));
      }

      final token = await user.getIdToken();
      final base64Image = base64Encode(imageBytes);

      final response = await _dio.post(
        '${_config.backendUrl}/api/v1/payroll/extract',
        data: {
          'imageBase64': base64Image,
          'mimeType': mimeType,
        },
        options: Options(
          headers: {
            'Authorization': 'Bearer $token',
            'Content-Type': 'application/json',
          },
          sendTimeout: _config.apiTimeout,
          receiveTimeout: _config.apiTimeout,
        ),
      );

      if (response.statusCode == 200) {
        final data = response.data as Map<String, dynamic>;

        return Right(
          ExtractedPayrollData(
            rawJson: data['extractedData'] as String,
            rawText: data['rawText'] as String,
            confidence: (data['confidence'] as num?)?.toDouble() ?? 0.0,
          ),
        );
      } else {
        return Left(
          NetworkFailure('Server returned ${response.statusCode}'),
        );
      }
    } on DioException catch (e) {
      return Left(_handleDioError(e));
    } catch (e) {
      return Left(GeminiFailure('Extraction failed: $e'));
    }
  }

  /// Process PDF file (backend handles PDF to image conversion)
  Future<Either<Failure, ExtractedPayrollData>> extractFromPdf(
    File pdfFile,
  ) async {
    try {
      final user = FirebaseAuth.instance.currentUser;
      if (user == null) {
        return const Left(AuthFailure('User not authenticated'));
      }

      final token = await user.getIdToken();
      final bytes = await pdfFile.readAsBytes();
      final base64Pdf = base64Encode(bytes);

      final response = await _dio.post(
        '${_config.backendUrl}/api/v1/payroll/extract-pdf',
        data: {
          'pdfBase64': base64Pdf,
          'fileName': pdfFile.path.split('/').last,
        },
        options: Options(
          headers: {
            'Authorization': 'Bearer $token',
            'Content-Type': 'application/json',
          },
          sendTimeout: const Duration(seconds: 60), // PDFs may take longer
          receiveTimeout: const Duration(seconds: 60),
        ),
      );

      if (response.statusCode == 200) {
        final data = response.data as Map<String, dynamic>;

        return Right(
          ExtractedPayrollData(
            rawJson: data['extractedData'] as String,
            rawText: data['rawText'] as String,
            confidence: (data['confidence'] as num?)?.toDouble() ?? 0.0,
          ),
        );
      } else {
        return Left(
          NetworkFailure('Server returned ${response.statusCode}'),
        );
      }
    } on DioException catch (e) {
      return Left(_handleDioError(e));
    } catch (e) {
      return Left(GeminiFailure('PDF extraction failed: $e'));
    }
  }

  /// Get extraction history (cached results from backend)
  Future<Either<Failure, List<Map<String, dynamic>>>> getExtractionHistory({
    int limit = 10,
  }) async {
    try {
      final user = FirebaseAuth.instance.currentUser;
      if (user == null) {
        return const Left(AuthFailure('User not authenticated'));
      }

      final token = await user.getIdToken();

      final response = await _dio.get(
        '${_config.backendUrl}/api/v1/payroll/history',
        queryParameters: {'limit': limit},
        options: Options(
          headers: {'Authorization': 'Bearer $token'},
        ),
      );

      if (response.statusCode == 200) {
        final data = response.data as List;
        return Right(data.cast<Map<String, dynamic>>());
      } else {
        return Left(
          NetworkFailure('Failed to fetch history'),
        );
      }
    } on DioException catch (e) {
      return Left(_handleDioError(e));
    } catch (e) {
      return Left(NetworkFailure('Failed to fetch history: $e'));
    }
  }

  String _getMimeType(String filePath) {
    final extension = filePath.split('.').last.toLowerCase();
    switch (extension) {
      case 'jpg':
      case 'jpeg':
        return 'image/jpeg';
      case 'png':
        return 'image/png';
      case 'webp':
        return 'image/webp';
      case 'pdf':
        return 'application/pdf';
      default:
        return 'image/jpeg';
    }
  }

  Failure _handleDioError(DioException e) {
    switch (e.type) {
      case DioExceptionType.connectionTimeout:
      case DioExceptionType.sendTimeout:
      case DioExceptionType.receiveTimeout:
        return const NetworkFailure(
          'Request timeout. Please check your connection.',
        );

      case DioExceptionType.badResponse:
        final statusCode = e.response?.statusCode;
        final message = e.response?.data?['message'] as String?;

        if (statusCode == 401) {
          return const AuthFailure('Authentication failed. Please login again.');
        } else if (statusCode == 429) {
          return const NetworkFailure(
            'Rate limit exceeded. Please try again later.',
          );
        } else if (statusCode == 503) {
          return const NetworkFailure(
            'Service temporarily unavailable. Please try again later.',
          );
        } else {
          return NetworkFailure(
            message ?? 'Server error (${statusCode ?? 'unknown'})',
          );
        }

      case DioExceptionType.cancel:
        return const NetworkFailure('Request cancelled');

      case DioExceptionType.connectionError:
        return const NetworkFailure(
          'Connection error. Please check your internet connection.',
        );

      case DioExceptionType.badCertificate:
        return const NetworkFailure('Security certificate error');

      case DioExceptionType.unknown:
      default:
        return NetworkFailure('Network error: ${e.message}');
    }
  }

  // Utility methods
  static bool validatePpsNumber(String ppsNumber) {
    final regex = RegExp(r'^\d{7}[A-Z]{1,2}$');
    return regex.hasMatch(ppsNumber.toUpperCase().replaceAll(' ', ''));
  }

  static String formatCurrency(double amount) {
    return '€${amount.toStringAsFixed(2)}';
  }
}

// Provider with environment configuration
final geminiServiceBackendProvider = Provider<GeminiServiceBackend>((ref) {
  final dio = Dio(
    BaseOptions(
      connectTimeout: const Duration(seconds: 30),
      receiveTimeout: const Duration(seconds: 30),
      validateStatus: (status) => status != null && status < 500,
    ),
  );

  // Add interceptors for logging in development
  if (EnvironmentConfig.development.enableLogging) {
    dio.interceptors.add(
      LogInterceptor(
        requestBody: true,
        responseBody: true,
        error: true,
      ),
    );
  }

  // Use the appropriate environment config
  const config = EnvironmentConfig.production; // Change based on build

  return GeminiServiceBackend(dio: dio, config: config);
});
