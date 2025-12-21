# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Irish Payslips Management System - A Spring Boot 3.2 application for Irish payroll processing with comprehensive tax compliance (PAYE, PRSI, USC). This system handles employee management, payroll calculations, payslip generation, and Revenue compliance reporting.

## Commands

### Development
```bash
mvn spring-boot:run              # Run application locally
mvn clean compile                # Clean and compile
mvn clean package                # Build JAR
```

### Testing
```bash
mvn test                         # Run all unit tests
mvn verify                       # Run integration tests
mvn test -Dtest=PayrollServiceTest  # Run specific test class
mvn jacoco:report                # Generate test coverage report
```

### Database Migrations
```bash
mvn liquibase:update             # Apply database migrations
mvn liquibase:status             # Check migration status
mvn liquibase:rollback           # Rollback last migration
```

### Maven Profiles
```bash
mvn spring-boot:run -P dev       # Run with dev profile (default)
mvn spring-boot:run -P test      # Run with test profile
mvn package -P prod              # Build with prod profile
```

## Architecture

### Layered Architecture Pattern
The application follows strict layered architecture with clear separation of concerns:

1. **Controller Layer** (`controller/`) - REST API endpoints with OpenAPI documentation
2. **Service Layer** (`service/`) - Business logic and transaction management
3. **Repository Layer** (`repository/`) - Data access abstraction using Spring Data JPA
4. **Entity Layer** (`entity/`) - JPA entities with audit trail support
5. **Security Layer** (`security/`) - JWT authentication and role-based access control

### Irish Tax Calculation Architecture
The tax calculation subsystem is organized into specialized services under `service/tax/`:
- `PayeCalculationService` - PAYE (Pay As You Earn) calculations
- `PrsiCalculationService` - PRSI (Pay Related Social Insurance) calculations
- `UscCalculationService` - USC (Universal Social Charge) calculations
- `TaxCreditService` - Tax credit management and application

Each tax service encapsulates Irish Revenue-specific logic, tax bands, rates, and exemption rules.

### Key Annotations in Use
The main application class enables several Spring features:
- `@EnableJpaAuditing` - Automatic created/modified date tracking on entities
- `@EnableCaching` - Caffeine cache for tax configuration and frequently accessed data
- `@EnableAsync` - Asynchronous processing for email and report generation
- `@EnableTransactionManagement` - Declarative transaction management

### Data Mapping Strategy
Uses MapStruct for type-safe object mapping between entities and DTOs. The annotation processor runs during compilation, requiring the `mapstruct-processor` to be configured in Maven's annotation processor path.

### Validation Architecture
Implements Bean Validation (JSR-303) with custom Irish-specific validators:
- `PpsNumberValidator` - Validates Irish Personal Public Service numbers
- `IrishBankAccountValidator` - Validates Irish bank account formats
- Custom validation annotations like `@ValidPpsNumber`

## Irish Payroll Domain Logic

### PPS Number Validation
Irish PPS (Personal Public Service) numbers follow a specific format (7 digits + 1-2 letters). The validation logic ensures compliance with Irish Revenue requirements.

### Tax Year Considerations
Irish tax years run from January 1 to December 31. The system handles:
- Mid-year tax rate changes
- Tax credit adjustments
- Year-to-date calculations for cumulative tax
- P60 generation at year-end

### Payroll Periods
Supports multiple payroll frequencies:
- Weekly (52 pay periods)
- Bi-weekly (26 pay periods)
- Monthly (12 pay periods)

Tax calculations must be adjusted based on the payroll period frequency.

## Technology Stack

### Core Framework
- Spring Boot 3.2.1 with Java 17
- Maven for build management
- Spring Web MVC for REST APIs

### Database
- **Development**: H2 in-memory database
- **Testing**: Testcontainers with PostgreSQL
- **Production**: PostgreSQL 15+ with HikariCP connection pooling
- **Migrations**: Liquibase for version-controlled schema changes

### Security
- Spring Security 6.x with JWT (JJWT 0.12.3)
- BCrypt password hashing
- Role-based access control

### Document Generation
- **PDF**: iText 8.0.2 for payslip generation
- **Excel**: Apache POI for reporting
- **Email Templates**: Thymeleaf

### Testing
- JUnit 5 with Mockito for mocking
- Spring Boot Test for integration testing
- Testcontainers for database integration tests
- WireMock for external service mocking

## Development Patterns

### AI Rules Integration
The `.ai-rules/` directory contains architectural guidance:
- `product.md` - Product vision, features, and target users
- `structure.md` - Package structure and naming conventions
- `tech.md` - Technology decisions and build commands

Refer to these files when adding features to ensure consistency with project standards.

### Feature Development Flow
1. Design entity in `entity/` package with appropriate JPA annotations
2. Create Spring Data JPA repository interface in `repository/`
3. Implement business logic in `service/` with `@Service` annotation
4. Add REST controller in `controller/` with OpenAPI annotations
5. Create request/response DTOs in `dto/request/` and `dto/response/`
6. Add MapStruct mapper interface in `mapper/`
7. Write comprehensive tests mirroring the main package structure

### Error Handling
Centralized exception handling via `GlobalExceptionHandler` using `@ControllerAdvice`. Domain-specific exceptions extend base `PayrollException`.

### Audit Trail
Entities extending `AuditableEntity` automatically track:
- Created date/time
- Created by user
- Last modified date/time
- Last modified by user

This is enabled via `@EnableJpaAuditing` on the main application class.

## Database Configuration

### Liquibase Migrations
Database changes are version-controlled in `src/main/resources/db/migration/`:
- Naming: `V{version}__{description}.sql` (e.g., `V1__create_employees_table.sql`)
- Never modify existing migrations in production
- Use `liquibase.properties` for configuration

### Environment-Specific Profiles
- `application-dev.yml` - H2 database, debug logging, relaxed security
- `application-test.yml` - Test containers configuration
- `application-prod.yml` - PostgreSQL, optimized connection pool, production logging

## Testing Strategy

### Unit Tests
Focus on service layer business logic with mocked dependencies. Test Irish tax calculations thoroughly with edge cases (tax band boundaries, exemptions, credits).

### Integration Tests
Use `@SpringBootTest` with Testcontainers for repository layer. Test complex queries and transactions.

### Test Data
Use `TestDataBuilder` pattern for creating complex test objects. Keep test fixtures in `src/test/resources/fixtures/`.

### Coverage
JaCoCo plugin generates coverage reports in `target/site/jacoco/`. Aim for >80% coverage on service and tax calculation classes.

## API Documentation

OpenAPI/Swagger UI available at `/swagger-ui.html` when running locally. API documentation is auto-generated from controller annotations.

## Performance Considerations

### Caching Strategy
Caffeine cache enabled for:
- Tax configuration (rates, bands, credits)
- Employee lookup by PPS number
- Frequently accessed reference data

Cache invalidation happens on administrative updates to tax configuration.

### Database Optimization
- Use `@EntityGraph` to avoid N+1 queries on entity relationships
- Leverage Spring Data JPA pagination for large result sets
- Index foreign keys and frequently queried columns

### Async Processing
Email sending and large report generation use `@Async` methods to avoid blocking request threads.

## Security Considerations

### JWT Authentication
Stateless authentication using JWT tokens. Token expiration and refresh logic in security layer.

### Role-Based Access
Roles: `ROLE_ADMIN`, `ROLE_PAYROLL_OFFICER`, `ROLE_HR_ADMIN`, `ROLE_EMPLOYEE`

Each endpoint is secured with appropriate role restrictions via `@PreAuthorize`.

### Sensitive Data
Employee salary, PPS numbers, and tax information are sensitive. Ensure:
- Encrypted at rest in production
- Not logged in application logs
- Masked in responses where appropriate
- GDPR compliance for data retention and deletion

## Common Development Tasks

### Adding a New Tax Calculation Rule
1. Update appropriate service in `service/tax/`
2. Add new tax rates/bands to `IrishTaxConstants` in `util/`
3. Create Liquibase migration to update `tax_configuration` table
4. Add comprehensive unit tests with boundary cases
5. Update API documentation if exposed via controller

### Generating New Reports
1. Create service in `service/pdf/` or use Apache POI for Excel
2. Add Thymeleaf template if needed in `resources/templates/`
3. Ensure async processing for large reports
4. Add controller endpoint with streaming response for downloads

### Custom Validation
1. Create validator class implementing `ConstraintValidator`
2. Define custom annotation in `validation/` package
3. Register annotation on DTO fields
4. Write unit tests for validator logic
