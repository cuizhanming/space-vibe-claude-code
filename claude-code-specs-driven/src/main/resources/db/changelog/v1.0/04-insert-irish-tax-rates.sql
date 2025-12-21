--liquibase formatted sql

--changeset irish-payroll:8
--comment: Insert 2025 PAYE tax rates

INSERT INTO tax_configurations (id, tax_year, tax_type, band_name, income_lower, income_upper, rate, is_active, effective_from, created_date, last_modified_date)
VALUES
    (RANDOM_UUID(), 2025, 'PAYE', 'Standard Rate', 0, 42000, 0.20, TRUE, '2025-01-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (RANDOM_UUID(), 2025, 'PAYE', 'Higher Rate', 42000, 999999999, 0.40, TRUE, '2025-01-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

--rollback DELETE FROM tax_configurations WHERE tax_year = 2025 AND tax_type = 'PAYE';

--changeset irish-payroll:9
--comment: Insert 2025 USC tax rates

INSERT INTO tax_configurations (id, tax_year, tax_type, band_name, income_lower, income_upper, rate, is_active, effective_from, created_date, last_modified_date)
VALUES
    (RANDOM_UUID(), 2025, 'USC', 'Band 1', 0, 12012, 0.005, TRUE, '2025-01-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (RANDOM_UUID(), 2025, 'USC', 'Band 2', 12012, 25760, 0.02, TRUE, '2025-01-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (RANDOM_UUID(), 2025, 'USC', 'Band 3', 25760, 70044, 0.04, TRUE, '2025-01-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (RANDOM_UUID(), 2025, 'USC', 'Band 4', 70044, 999999999, 0.08, TRUE, '2025-01-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

--rollback DELETE FROM tax_configurations WHERE tax_year = 2025 AND tax_type = 'USC';

--changeset irish-payroll:10
--comment: Insert 2025 PRSI rates

INSERT INTO tax_configurations (id, tax_year, tax_type, band_name, income_lower, income_upper, rate, is_active, effective_from, created_date, last_modified_date)
VALUES
    (RANDOM_UUID(), 2025, 'PRSI', 'Employee Class A', 0, 999999999, 0.04, TRUE, '2025-01-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

--rollback DELETE FROM tax_configurations WHERE tax_year = 2025 AND tax_type = 'PRSI';
