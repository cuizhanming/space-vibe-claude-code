package com.irish.payroll.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validator for Irish PPS (Personal Public Service) numbers.
 * Valid format: 7 digits followed by 1-2 uppercase letters
 * Example: 1234567AB or 1234567A
 */
public class PpsNumberValidator implements ConstraintValidator<ValidPpsNumber, String> {

    private static final Pattern PPS_PATTERN = Pattern.compile("^\\d{7}[A-Z]{1,2}$");

    @Override
    public void initialize(ValidPpsNumber constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String ppsNumber, ConstraintValidatorContext context) {
        if (ppsNumber == null || ppsNumber.isBlank()) {
            return false;
        }

        return PPS_PATTERN.matcher(ppsNumber.trim()).matches();
    }
}
