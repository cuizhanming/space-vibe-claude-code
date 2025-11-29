import 'package:dartz/dartz.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb_auth;
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/errors/failures.dart';
import '../../domain/entities/user_entity.dart';
import '../../domain/repositories/auth_repository.dart';

class AuthRepositoryImpl implements AuthRepository {
  AuthRepositoryImpl(this._firebaseAuth);

  final fb_auth.FirebaseAuth _firebaseAuth;

  @override
  Stream<UserEntity?> get authStateChanges {
    return _firebaseAuth.authStateChanges().map((user) {
      if (user == null) return null;
      return UserEntity(
        uid: user.uid,
        email: user.email ?? '',
        displayName: user.displayName,
        photoUrl: user.photoURL,
        emailVerified: user.emailVerified,
      );
    });
  }

  @override
  Future<Either<Failure, UserEntity>> signInWithEmailAndPassword({
    required String email,
    required String password,
  }) async {
    try {
      final result = await _firebaseAuth.signInWithEmailAndPassword(
        email: email,
        password: password,
      );

      if (result.user == null) {
        return Left(AuthFailure('Failed to sign in'));
      }

      return Right(
        UserEntity(
          uid: result.user!.uid,
          email: result.user!.email ?? '',
          displayName: result.user!.displayName,
          photoUrl: result.user!.photoURL,
          emailVerified: result.user!.emailVerified,
        ),
      );
    } on fb_auth.FirebaseAuthException catch (e) {
      return Left(AuthFailure(_mapFirebaseError(e)));
    } catch (e) {
      return Left(AuthFailure('An unexpected error occurred: $e'));
    }
  }

  @override
  Future<Either<Failure, UserEntity>> registerWithEmailAndPassword({
    required String email,
    required String password,
    String? displayName,
  }) async {
    try {
      final result = await _firebaseAuth.createUserWithEmailAndPassword(
        email: email,
        password: password,
      );

      if (result.user == null) {
        return Left(AuthFailure('Failed to create account'));
      }

      // Update display name if provided
      if (displayName != null && displayName.isNotEmpty) {
        await result.user!.updateDisplayName(displayName);
        await result.user!.reload();
      }

      // Send email verification
      await result.user!.sendEmailVerification();

      return Right(
        UserEntity(
          uid: result.user!.uid,
          email: result.user!.email ?? '',
          displayName: result.user!.displayName,
          photoUrl: result.user!.photoURL,
          emailVerified: result.user!.emailVerified,
        ),
      );
    } on fb_auth.FirebaseAuthException catch (e) {
      return Left(AuthFailure(_mapFirebaseError(e)));
    } catch (e) {
      return Left(AuthFailure('An unexpected error occurred: $e'));
    }
  }

  @override
  Future<Either<Failure, void>> signOut() async {
    try {
      await _firebaseAuth.signOut();
      return const Right(null);
    } catch (e) {
      return Left(AuthFailure('Failed to sign out: $e'));
    }
  }

  @override
  Future<Either<Failure, void>> sendPasswordResetEmail(String email) async {
    try {
      await _firebaseAuth.sendPasswordResetEmail(email: email);
      return const Right(null);
    } on fb_auth.FirebaseAuthException catch (e) {
      return Left(AuthFailure(_mapFirebaseError(e)));
    } catch (e) {
      return Left(AuthFailure('Failed to send password reset email: $e'));
    }
  }

  @override
  UserEntity? get currentUser {
    final user = _firebaseAuth.currentUser;
    if (user == null) return null;

    return UserEntity(
      uid: user.uid,
      email: user.email ?? '',
      displayName: user.displayName,
      photoUrl: user.photoURL,
      emailVerified: user.emailVerified,
    );
  }

  String _mapFirebaseError(fb_auth.FirebaseAuthException e) {
    switch (e.code) {
      case 'user-not-found':
        return 'No user found with this email';
      case 'wrong-password':
        return 'Incorrect password';
      case 'email-already-in-use':
        return 'An account already exists with this email';
      case 'invalid-email':
        return 'Invalid email address';
      case 'weak-password':
        return 'Password is too weak';
      case 'user-disabled':
        return 'This account has been disabled';
      case 'too-many-requests':
        return 'Too many failed attempts. Please try again later';
      case 'operation-not-allowed':
        return 'This operation is not allowed';
      default:
        return e.message ?? 'An authentication error occurred';
    }
  }
}

final authRepositoryProvider = Provider<AuthRepository>((ref) {
  return AuthRepositoryImpl(fb_auth.FirebaseAuth.instance);
});
