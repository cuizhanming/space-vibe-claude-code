package com.irish.payroll.service.tax;

import com.irish.payroll.entity.PayFrequency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PRSI Calculation Service.
 */
class PrsiCalculationServiceTest {

    private PrsiCalculationService prsiService;

    @BeforeEach
    void setUp() {
        prsiService = new PrsiCalculationService();
    }

    @Test
    void testCalculatePrsi_Weekly_AboveThreshold() {
        BigDecimal grossPay = new BigDecimal("500");

        // Expected: 500 * 0.04 = 20.00
        BigDecimal prsi = prsiService.calculateEmployeePrsi(grossPay, PayFrequency.WEEKLY);

        assertEquals(new BigDecimal("20.00"), prsi);
    }

    @Test
    void testCalculatePrsi_Weekly_BelowThreshold() {
        BigDecimal grossPay = new BigDecimal("300");

        // Expected: 0 (below €352 threshold)
        BigDecimal prsi = prsiService.calculateEmployeePrsi(grossPay, PayFrequency.WEEKLY);

        assertEquals(BigDecimal.ZERO, prsi);
    }

    @Test
    void testCalculatePrsi_Weekly_AtThreshold() {
        BigDecimal grossPay = new BigDecimal("352");

        // Expected: 352 * 0.04 = 14.08
        BigDecimal prsi = prsiService.calculateEmployeePrsi(grossPay, PayFrequency.WEEKLY);

        assertEquals(new BigDecimal("14.08"), prsi);
    }

    @Test
    void testCalculatePrsi_Monthly_AboveThreshold() {
        BigDecimal grossPay = new BigDecimal("3000");

        // Expected: 3000 * 0.04 = 120.00
        BigDecimal prsi = prsiService.calculateEmployeePrsi(grossPay, PayFrequency.MONTHLY);

        assertEquals(new BigDecimal("120.00"), prsi);
    }

    @Test
    void testCalculatePrsi_Monthly_BelowThreshold() {
        BigDecimal grossPay = new BigDecimal("1000");

        // Expected: 0 (below €1,526 threshold)
        BigDecimal prsi = prsiService.calculateEmployeePrsi(grossPay, PayFrequency.MONTHLY);

        assertEquals(BigDecimal.ZERO, prsi);
    }

    @Test
    void testCalculatePrsi_Monthly_AtThreshold() {
        BigDecimal grossPay = new BigDecimal("1526");

        // Expected: 1526 * 0.04 = 61.04
        BigDecimal prsi = prsiService.calculateEmployeePrsi(grossPay, PayFrequency.MONTHLY);

        assertEquals(new BigDecimal("61.04"), prsi);
    }

    @Test
    void testCalculatePrsi_ZeroIncome() {
        BigDecimal prsi = prsiService.calculateEmployeePrsi(BigDecimal.ZERO, PayFrequency.WEEKLY);

        assertEquals(BigDecimal.ZERO, prsi);
    }

    @Test
    void testCalculatePrsi_NullIncome() {
        BigDecimal prsi = prsiService.calculateEmployeePrsi(null, PayFrequency.WEEKLY);

        assertEquals(BigDecimal.ZERO, prsi);
    }
}
