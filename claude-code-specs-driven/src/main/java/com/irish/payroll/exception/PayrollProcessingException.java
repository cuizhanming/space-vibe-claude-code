package com.irish.payroll.exception;

/**
 * Exception thrown when payroll processing fails.
 */
public class PayrollProcessingException extends RuntimeException {

    public PayrollProcessingException(String message) {
        super(message);
    }

    public PayrollProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
