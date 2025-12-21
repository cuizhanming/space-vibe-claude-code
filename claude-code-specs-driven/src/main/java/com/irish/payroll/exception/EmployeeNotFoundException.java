package com.irish.payroll.exception;

import java.util.UUID;

/**
 * Exception thrown when an employee is not found in the system.
 */
public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(UUID id) {
        super("Employee not found with id: " + id);
    }

    public EmployeeNotFoundException(String message) {
        super(message);
    }
}
