# Spec 002: Payroll Processing — Requirements

**Status**: 📝 Draft
**Created**: 2025-05-08
**Phase**: Requirements → Design → Tasks → Implementation

---

## Feature Summary

End-to-end payroll run processing: calculate PAYE/PRSI/USC for all active employees, generate payslips, and produce Revenue-compliant summary reports.

---

## Requirements (EARS Format)

### Payroll Run

**WHEN** a Payroll Officer initiates a payroll run for a pay period  
**THEN** the system SHALL calculate net pay for all active employees in that period  
**AND** apply current Irish tax rates (PAYE bands, PRSI class, USC rates)  
**AND** apply each employee's individual tax credits  

**WHEN** a payroll run completes successfully  
**THEN** the system SHALL generate a payslip PDF for each employee  
**AND** update the year-to-date (YTD) totals for each employee  
**AND** mark the payroll run status as COMPLETED  

**IF** any tax calculation fails during a payroll run  
**THEN** the system SHALL mark that employee's payslip as FAILED  
**AND** continue processing remaining employees  
**AND** include failed employees in the run summary report  

**WHEN** a payroll run is in DRAFT status  
**THEN** a Payroll Officer SHALL be able to review and modify individual payslips  
**BEFORE** the run is finalised  

**WHEN** a payroll run is FINALISED  
**THEN** no further modifications SHALL be permitted  
**AND** payslips SHALL be made available to employees  

### Tax Calculations

**WHEN** calculating PAYE for an employee  
**THEN** the system SHALL apply the correct tax band rates (20% / 40%)  
**AND** subtract the employee's annual tax credit (pro-rated for pay frequency)  
**AND** account for year-to-date earnings to determine correct band  

**WHEN** calculating PRSI  
**THEN** the system SHALL apply the correct PRSI class rate (default: Class A1 at 4%)  
**AND** calculate employer PRSI contribution (11.05%)  

**WHEN** calculating USC  
**THEN** the system SHALL apply the 4-band USC rates  
**AND** apply medical card exemptions where applicable  

### Non-Functional Requirements

- Payroll run for 500 employees: complete within 30 seconds
- Tax calculations must match Revenue test cases to 6 decimal places
- All payroll runs must be fully auditable and reversible (void + rerun)

---

## Acceptance Criteria

- [ ] POST /api/v1/payroll/runs initiates a payroll run
- [ ] Payroll run processes all ACTIVE employees for the period
- [ ] PAYE calculated correctly for standard and higher rate taxpayers
- [ ] PRSI Class A1 calculated correctly (employee + employer)
- [ ] USC bands applied correctly
- [ ] Tax credits pro-rated correctly for weekly/monthly frequency
- [ ] YTD totals updated after run completion
- [ ] PDF payslips generated for all employees
- [ ] Run summary report available via GET /api/v1/payroll/runs/{id}/summary
