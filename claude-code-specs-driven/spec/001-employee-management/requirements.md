# Spec 001: Employee Management — Requirements

**Status**: ✅ Approved
**Created**: 2025-05-08
**Phase**: Requirements → Design → Tasks → Implementation

---

## Feature Summary

Full lifecycle management of employee records with Irish-specific compliance fields (PPS number, tax credits, PRSI class).

---

## Requirements (EARS Format)

### Functional Requirements

**WHEN** an HR Administrator submits a new employee form  
**THEN** the system SHALL validate the PPS number format and uniqueness  
**AND** create an employee record with a generated system ID  
**AND** return a 201 Created response with the employee resource  

**WHEN** a PPS number fails validation  
**THEN** the system SHALL return a 400 error with the specific validation failure message  

**IF** an employee with the same PPS number already exists  
**THEN** the system SHALL reject the request with a 409 Conflict response  

**WHEN** a Payroll Officer requests the employee list  
**THEN** the system SHALL return paginated results (default page size: 20)  
**AND** support filtering by: department, employment status, payroll frequency  

**WHEN** an HR Administrator updates an employee's tax credit  
**THEN** the system SHALL log the change with timestamp, old value, and new value  
**AND** apply the new credit to the next payroll run  

**WHEN** an employee is marked as terminated  
**THEN** the system SHALL set the termination date  
**AND** prevent future payroll runs for that employee  
**AND** retain all historical payslip data  

### Non-Functional Requirements

- PPS validation response time: < 50ms
- Employee list API: < 200ms for up to 1000 records
- All employee mutations must be auditable (who changed what, when)
- PPS numbers must be encrypted at rest (AES-256)

---

## Acceptance Criteria

- [ ] POST /api/v1/employees creates employee with valid PPS
- [ ] POST /api/v1/employees returns 400 for invalid PPS format
- [ ] POST /api/v1/employees returns 409 for duplicate PPS
- [ ] GET /api/v1/employees returns paginated list
- [ ] GET /api/v1/employees supports `?status=active` filter
- [ ] PUT /api/v1/employees/{id} updates employee and logs audit event
- [ ] DELETE /api/v1/employees/{id} performs soft delete (sets terminatedAt)
- [ ] Audit log entries are created for all mutations

---

## Out of Scope

- Employee self-service portal (covered in separate spec)
- P60 generation (end-of-year spec)
- Bulk employee import via CSV
