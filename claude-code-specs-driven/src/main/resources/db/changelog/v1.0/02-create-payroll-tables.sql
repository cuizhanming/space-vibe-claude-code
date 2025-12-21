--liquibase formatted sql

--changeset irish-payroll:3
--comment: Create payrolls table

CREATE TABLE payrolls (
    id UUID PRIMARY KEY,
    pay_period_start DATE NOT NULL,
    pay_period_end DATE NOT NULL,
    payment_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('DRAFT', 'PROCESSED', 'PAID')),
    total_gross DECIMAL(12,2),
    total_paye DECIMAL(12,2),
    total_prsi DECIMAL(12,2),
    total_usc DECIMAL(12,2),
    total_net DECIMAL(12,2),
    processed_date TIMESTAMP,
    created_date TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),
    CONSTRAINT unique_payroll_period UNIQUE (pay_period_start, pay_period_end)
);

--rollback DROP TABLE payrolls;

--changeset irish-payroll:4
--comment: Create payslips table

CREATE TABLE payslips (
    id UUID PRIMARY KEY,
    payroll_id UUID NOT NULL,
    employee_id UUID NOT NULL,
    gross_pay DECIMAL(10,2) NOT NULL,
    paye_deduction DECIMAL(10,2) DEFAULT 0,
    prsi_deduction DECIMAL(10,2) DEFAULT 0,
    usc_deduction DECIMAL(10,2) DEFAULT 0,
    net_pay DECIMAL(10,2) NOT NULL,
    tax_credits_used DECIMAL(10,2) DEFAULT 0,
    ytd_gross DECIMAL(12,2),
    ytd_paye DECIMAL(12,2),
    ytd_prsi DECIMAL(12,2),
    ytd_usc DECIMAL(12,2),
    ytd_net DECIMAL(12,2),
    created_date TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),
    CONSTRAINT fk_payslip_payroll FOREIGN KEY (payroll_id) REFERENCES payrolls(id),
    CONSTRAINT fk_payslip_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
);

--rollback DROP TABLE payslips;

--changeset irish-payroll:5
--comment: Create indexes on payroll tables

CREATE INDEX idx_payrolls_period ON payrolls(pay_period_start, pay_period_end);
CREATE INDEX idx_payrolls_status ON payrolls(status);
CREATE INDEX idx_payslips_payroll ON payslips(payroll_id);
CREATE INDEX idx_payslips_employee ON payslips(employee_id);
CREATE INDEX idx_payslips_created_date ON payslips(created_date);

--rollback DROP INDEX idx_payrolls_period;
--rollback DROP INDEX idx_payrolls_status;
--rollback DROP INDEX idx_payslips_payroll;
--rollback DROP INDEX idx_payslips_employee;
--rollback DROP INDEX idx_payslips_created_date;
