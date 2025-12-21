package com.irish.payroll.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PPS Number Validator.
 */
class PpsNumberValidatorTest {

    private PpsNumberValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PpsNumberValidator();
    }

    @Test
    void testValidPpsNumber_SevenDigitsTwoLetters() {
        assertTrue(validator.isValid("1234567AB", null));
    }

    @Test
    void testValidPpsNumber_SevenDigitsOneLetter() {
        assertTrue(validator.isValid("1234567A", null));
    }

    @Test
    void testValidPpsNumber_RealFormat() {
        assertTrue(validator.isValid("9876543TW", null));
    }

    @Test
    void testInvalidPpsNumber_TooShort() {
        assertFalse(validator.isValid("123456A", null));
    }

    @Test
    void testInvalidPpsNumber_TooLong() {
        assertFalse(validator.isValid("12345678ABC", null));
    }

    @Test
    void testInvalidPpsNumber_NoLetters() {
        assertFalse(validator.isValid("1234567", null));
    }

    @Test
    void testInvalidPpsNumber_OnlyLetters() {
        assertFalse(validator.isValid("ABCDEFG", null));
    }

    @Test
    void testInvalidPpsNumber_LowercaseLetters() {
        assertFalse(validator.isValid("1234567ab", null));
    }

    @Test
    void testInvalidPpsNumber_Null() {
        assertFalse(validator.isValid(null, null));
    }

    @Test
    void testInvalidPpsNumber_Empty() {
        assertFalse(validator.isValid("", null));
    }

    @Test
    void testInvalidPpsNumber_Blank() {
        assertFalse(validator.isValid("   ", null));
    }

    @Test
    void testValidPpsNumber_WithWhitespace() {
        assertTrue(validator.isValid(" 1234567AB ", null));
    }
}
