enum Environment { development, staging, production }

class EnvironmentConfig {
  const EnvironmentConfig({
    required this.environment,
    required this.backendUrl,
    required this.appName,
    required this.enableLogging,
    required this.enableAnalytics,
    required this.apiTimeout,
  });

  final Environment environment;
  final String backendUrl;
  final String appName;
  final bool enableLogging;
  final bool enableAnalytics;
  final Duration apiTimeout;

  // Development configuration
  static const development = EnvironmentConfig(
    environment: Environment.development,
    backendUrl: String.fromEnvironment(
      'BACKEND_URL',
      defaultValue: 'http://localhost:3000',
    ),
    appName: 'Payroll Scanner Dev',
    enableLogging: true,
    enableAnalytics: false,
    apiTimeout: Duration(seconds: 30),
  );

  // Staging configuration
  static const staging = EnvironmentConfig(
    environment: Environment.staging,
    backendUrl: String.fromEnvironment(
      'BACKEND_URL',
      defaultValue: 'https://staging-api.example.com',
    ),
    appName: 'Payroll Scanner Staging',
    enableLogging: true,
    enableAnalytics: true,
    apiTimeout: Duration(seconds: 20),
  );

  // Production configuration
  static const production = EnvironmentConfig(
    environment: Environment.production,
    backendUrl: String.fromEnvironment(
      'BACKEND_URL',
      defaultValue: 'https://api.example.com',
    ),
    appName: 'Payroll Scanner',
    enableLogging: false,
    enableAnalytics: true,
    apiTimeout: Duration(seconds: 15),
  );

  bool get isDevelopment => environment == Environment.development;
  bool get isStaging => environment == Environment.staging;
  bool get isProduction => environment == Environment.production;

  @override
  String toString() {
    return 'Environment: ${environment.name}\n'
        'Backend URL: $backendUrl\n'
        'App Name: $appName\n'
        'Logging: $enableLogging\n'
        'Analytics: $enableAnalytics';
  }
}
