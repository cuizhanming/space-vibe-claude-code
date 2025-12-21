--liquibase formatted sql

--changeset irish-payroll:11
--comment: Create users table for authentication

CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    employee_id UUID,
    enabled BOOLEAN DEFAULT TRUE,
    created_date TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),
    CONSTRAINT fk_user_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
);

--rollback DROP TABLE users;

--changeset irish-payroll:12
--comment: Create indexes on users table

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_employee ON users(employee_id);

--rollback DROP INDEX idx_users_username;
--rollback DROP INDEX idx_users_email;
--rollback DROP INDEX idx_users_employee;

--changeset irish-payroll:13
--comment: Insert default admin user (password: admin123)

INSERT INTO users (id, username, password_hash, email, enabled, created_date, last_modified_date)
VALUES
    (RANDOM_UUID(), 'admin', '$2a$10$eACCYoNOHEqXve/ZYP6VleH2PYnBRmQvJKx6YPZgLNQFDK4mzCvLy', 'admin@irishpayroll.com', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

--rollback DELETE FROM users WHERE username = 'admin';
