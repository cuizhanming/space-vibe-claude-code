--liquibase formatted sql

--changeset irish-payroll:6
--comment: Create tax_configurations table

CREATE TABLE tax_configurations (
    id UUID PRIMARY KEY,
    tax_year INTEGER NOT NULL,
    tax_type VARCHAR(20) NOT NULL CHECK (tax_type IN ('PAYE', 'PRSI', 'USC')),
    band_name VARCHAR(50),
    income_lower DECIMAL(15,2),
    income_upper DECIMAL(15,2),
    rate DECIMAL(5,4) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    effective_from DATE NOT NULL,
    effective_to DATE,
    created_date TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),
    CONSTRAINT unique_tax_band UNIQUE (tax_year, tax_type, band_name)
);

--rollback DROP TABLE tax_configurations;

--changeset irish-payroll:7
--comment: Create indexes on tax_configurations table

CREATE INDEX idx_tax_config_year_type ON tax_configurations(tax_year, tax_type, is_active);
CREATE INDEX idx_tax_config_effective ON tax_configurations(effective_from, effective_to);

--rollback DROP INDEX idx_tax_config_year_type;
--rollback DROP INDEX idx_tax_config_effective;
