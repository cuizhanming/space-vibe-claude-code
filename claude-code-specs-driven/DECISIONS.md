# DECISIONS.md — Architectural Decision Log

This file records significant architectural decisions made during the development of the Irish Payslips Management System.
Update this after each spec is completed.

---

## Decision Format

```
## [ID]: [Short Title]
**Date**: YYYY-MM-DD
**Status**: Accepted | Superseded | Deprecated
**Context**: Why this decision was needed
**Decision**: What was chosen
**Alternatives Considered**: What was rejected and why
**Consequences**: Trade-offs and implications
```

---

## ADR-001: UUID Primary Keys for Employee Entity

**Date**: 2025-05-08
**Status**: Accepted

**Context**: Need to choose a primary key strategy for the `employees` table. Sequential integer IDs are simple but expose record counts and enable enumeration attacks on employee IDs in the API.

**Decision**: Use `UUID` (v4, database-generated via `gen_random_uuid()`) as the primary key for all main entities.

**Alternatives Considered**:
- Sequential `BIGINT` — rejected: enumerable, leaks data about employee count
- ULID — considered but adds a dependency; UUID is native to PostgreSQL

**Consequences**:
- URLs like `/employees/123` are no longer possible (good for security)
- Slightly larger index size than integer keys (negligible at scale)
- Joins are slightly slower — acceptable for this domain

---

## ADR-002: Separate Tax Calculation Services per Tax Type

**Date**: 2025-05-08
**Status**: Accepted

**Context**: Irish payroll has three distinct tax calculations: PAYE, PRSI, USC. Each has different bands, rates, rules, and Revenue update cycles.

**Decision**: Create dedicated service classes: `PayeCalculationService`, `PrsiCalculationService`, `UscCalculationService` rather than a single `TaxCalculationService`.

**Alternatives Considered**:
- Single `TaxCalculationService` with switch/if logic — rejected: would become a god class; harder to test individual tax types
- Strategy pattern with injected calculators — over-engineered for current scope; can refactor later

**Consequences**:
- Each tax type is independently testable (confirmed: separate test classes exist)
- Easier to update when Revenue changes one tax without risking the others
- Slight duplication in how tax configuration is fetched — acceptable

---

## ADR-003: Soft Delete for Employee Termination

**Date**: 2025-05-08
**Status**: Accepted

**Context**: When an employee is terminated, historical payslips must remain accessible. Hard delete would cascade-delete payslip history.

**Decision**: Implement soft delete via `terminated_at` timestamp. Active employees have `terminated_at = NULL`. Terminated employees have a termination timestamp.

**Alternatives Considered**:
- Archive table (move to `terminated_employees`) — more complex, requires data migration queries to join history
- `status` ENUM only — status can change; timestamp provides audit evidence of when termination occurred

**Consequences**:
- All queries for "active employees" must include `WHERE terminated_at IS NULL`
- Spring Data repository needs `@Query` or `findByTerminatedAtIsNull()` naming conventions

---

## ADR-004: Liquibase for Database Migrations

**Date**: 2025-05-08
**Status**: Accepted

**Context**: Database schema needs to evolve over time as tax rules, new features, and bug fixes require schema changes. Need version-controlled, repeatable migrations.

**Decision**: Use Liquibase with SQL-format changelogs (`db/changelog/v1.0/`).

**Alternatives Considered**:
- Flyway — similar capability; Liquibase chosen for rollback support and XML/SQL flexibility
- JPA `ddl-auto: update` — rejected: dangerous in production, no audit trail

**Consequences**:
- All schema changes must go through a Liquibase migration file
- Never modify an existing migration that has been applied to any environment
- `liquibase:rollback` available for emergency schema rollbacks

---

## ADR-005: Specs-Driven Development Workflow

**Date**: 2025-05-08
**Status**: Accepted

**Context**: This project is a vibe coding template demonstrating best practices for Claude Code. The goal is to show how structured specs prevent the common vibe coding failure modes: scope creep, architectural drift, incomplete implementations.

**Decision**: Adopt a 4-phase spec workflow: Requirements (EARS format) → Design (Mermaid diagrams) → Tasks (atomic checklist) → Implementation. Each phase requires explicit approval before proceeding.

**Alternatives Considered**:
- Freeform prompting — rejected: demonstrates bad practice, not useful as a template
- Detailed upfront design doc only — missing the iterative spec → approve → implement loop

**Consequences**:
- Each feature has living documentation in `spec/[ID]/`
- New contributors can understand what was built and why
- Claude Code sessions are more focused and context-efficient

---

_Add new ADRs as significant decisions are made during development._
