package com.irish.payroll.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Annotation for validating Irish PPS (Personal Public Service) numbers.
 * Valid format: 7 digits followed by 1-2 letters (e.g., 1234567AB)
 */
@Documented
@Constraint(validatedBy = PpsNumberValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPpsNumber {

    String message() default "Invalid PPS number format. Expected format: 7 digits followed by 1-2 letters (e.g., 1234567AB)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
