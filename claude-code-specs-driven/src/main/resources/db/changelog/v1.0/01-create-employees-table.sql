--liquibase formatted sql

--changeset irish-payroll:1
--comment: Create employees table

CREATE TABLE employees (
    id UUID PRIMARY KEY,
    pps_number VARCHAR(10) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    date_of_birth DATE,
    hire_date DATE NOT NULL,
    job_title VARCHAR(100),
    department VARCHAR(100),
    gross_salary DECIMAL(10,2) NOT NULL,
    pay_frequency VARCHAR(20) NOT NULL CHECK (pay_frequency IN ('WEEKLY', 'MONTHLY')),
    bank_account_number VARCHAR(20),
    tax_credits_annual DECIMAL(10,2) DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_date TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100)
);

--rollback DROP TABLE employees;

--changeset irish-payroll:2
--comment: Create indexes on employees table

CREATE INDEX idx_employees_pps ON employees(pps_number);
CREATE INDEX idx_employees_email ON employees(email);
CREATE INDEX idx_employees_active ON employees(is_active);
CREATE INDEX idx_employees_department ON employees(department);

--rollback DROP INDEX idx_employees_pps;
--rollback DROP INDEX idx_employees_email;
--rollback DROP INDEX idx_employees_active;
--rollback DROP INDEX idx_employees_department;
