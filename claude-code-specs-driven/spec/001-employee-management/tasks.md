# Spec 001: Employee Management — Tasks

**Status**: 🔄 In Progress
**Depends on**: design.md ✅

---

## Task Checklist

Tasks are atomic — each should be completable in a single Claude Code session or less.

### Phase 1: Data Layer
- [x] **Task 1.1** — Create `Employee` JPA entity with all fields from design schema
- [x] **Task 1.2** — Create Liquibase migration `V1__create_employees_table.sql`
- [x] **Task 1.3** — Create `EmployeeRepository` with custom queries (findByPpsNumber, findByStatus)
- [ ] **Task 1.4** — Add PPS number encryption/decryption in entity lifecycle hooks (`@PrePersist`, `@PostLoad`)

### Phase 2: Service Layer
- [x] **Task 2.1** — Implement `EmployeeService.createEmployee()` with PPS uniqueness check
- [x] **Task 2.2** — Implement `EmployeeService.updateEmployee()` with audit logging
- [x] **Task 2.3** — Implement `EmployeeService.terminateEmployee()` (soft delete)
- [ ] **Task 2.4** — Implement `EmployeeService.listEmployees()` with pagination + filtering
- [ ] **Task 2.5** — Create `AuditService.logEmployeeChange()` writing to audit_log table

### Phase 3: Controller Layer
- [x] **Task 3.1** — Create `EmployeeController` with POST/GET/PUT/DELETE endpoints
- [ ] **Task 3.2** — Add PATCH endpoint for partial updates (tax credits, PRSI class)
- [ ] **Task 3.3** — Add GET `/employees/{id}/audit` endpoint
- [x] **Task 3.4** — Add OpenAPI annotations to all endpoints

### Phase 4: Validation
- [x] **Task 4.1** — `PpsNumberValidator` — validates 7 digits + 1-2 letters format
- [ ] **Task 4.2** — `PpsNumberValidator` — add checksum validation per Revenue spec
- [ ] **Task 4.3** — Add `@ValidPpsNumber` to `EmployeeCreateRequest` DTO

### Phase 5: Tests
- [ ] **Task 5.1** — Unit tests for `PpsNumberValidator` (valid, invalid format, checksum edge cases)
- [ ] **Task 5.2** — Unit tests for `EmployeeService` (createEmployee, terminateEmployee)
- [ ] **Task 5.3** — Integration test: POST /employees → verify DB state + audit log
- [ ] **Task 5.4** — Integration test: duplicate PPS returns 409

---

## Dependencies

- Task 2.1 depends on: 1.1, 1.3
- Task 3.1 depends on: 2.1, 2.2, 2.3
- Task 5.3 depends on: 3.1, 2.5

---

## Progress

**Completed**: 7/18 tasks
**Blocked**: Task 1.4 (encryption library choice pending)
