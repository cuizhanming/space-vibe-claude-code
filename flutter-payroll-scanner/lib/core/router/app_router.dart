import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../features/auth/presentation/pages/login_page.dart';
import '../../features/auth/presentation/pages/register_page.dart';
import '../../features/auth/presentation/providers/auth_provider.dart';
import '../../features/home/presentation/pages/home_page.dart';
import '../../features/payroll/presentation/pages/payroll_detail_page.dart';
import '../../features/payroll/presentation/pages/payroll_list_page.dart';
import '../../features/payroll/presentation/pages/scan_document_page.dart';

enum AppRoute {
  login,
  register,
  home,
  payrollList,
  payrollDetail,
  scanDocument,
}

final routerProvider = Provider<GoRouter>((ref) {
  final authState = ref.watch(authStateProvider);

  return GoRouter(
    initialLocation: '/login',
    debugLogDiagnostics: true,
    redirect: (context, state) {
      final isLoggedIn = authState.asData?.value != null;
      final isLoggingIn = state.matchedLocation == '/login' ||
          state.matchedLocation == '/register';

      if (!isLoggedIn && !isLoggingIn) {
        return '/login';
      }

      if (isLoggedIn && isLoggingIn) {
        return '/home';
      }

      return null;
    },
    routes: [
      GoRoute(
        path: '/login',
        name: AppRoute.login.name,
        pageBuilder: (context, state) => MaterialPage(
          key: state.pageKey,
          child: const LoginPage(),
        ),
      ),
      GoRoute(
        path: '/register',
        name: AppRoute.register.name,
        pageBuilder: (context, state) => MaterialPage(
          key: state.pageKey,
          child: const RegisterPage(),
        ),
      ),
      GoRoute(
        path: '/home',
        name: AppRoute.home.name,
        pageBuilder: (context, state) => MaterialPage(
          key: state.pageKey,
          child: const HomePage(),
        ),
        routes: [
          GoRoute(
            path: 'payroll',
            name: AppRoute.payrollList.name,
            pageBuilder: (context, state) => MaterialPage(
              key: state.pageKey,
              child: const PayrollListPage(),
            ),
          ),
          GoRoute(
            path: 'payroll/:id',
            name: AppRoute.payrollDetail.name,
            pageBuilder: (context, state) {
              final id = state.pathParameters['id']!;
              return MaterialPage(
                key: state.pageKey,
                child: PayrollDetailPage(payrollId: id),
              );
            },
          ),
          GoRoute(
            path: 'scan',
            name: AppRoute.scanDocument.name,
            pageBuilder: (context, state) => MaterialPage(
              key: state.pageKey,
              child: const ScanDocumentPage(),
            ),
          ),
        ],
      ),
    ],
  );
});
