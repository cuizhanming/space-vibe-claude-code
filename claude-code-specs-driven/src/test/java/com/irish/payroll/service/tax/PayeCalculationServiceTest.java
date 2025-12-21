package com.irish.payroll.service.tax;

import com.irish.payroll.entity.TaxConfiguration;
import com.irish.payroll.entity.TaxType;
import com.irish.payroll.repository.TaxConfigurationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit tests for PAYE Calculation Service.
 */
@ExtendWith(MockitoExtension.class)
class PayeCalculationServiceTest {

    @Mock
    private TaxConfigurationRepository taxConfigRepository;

    @InjectMocks
    private PayeCalculationService payeService;

    private List<TaxConfiguration> mockPayeBands;

    @BeforeEach
    void setUp() {
        // Create mock PAYE tax bands for 2025
        TaxConfiguration standardRate = new TaxConfiguration();
        standardRate.setTaxYear(2025);
        standardRate.setTaxType(TaxType.PAYE);
        standardRate.setBandName("Standard Rate");
        standardRate.setIncomeLower(new BigDecimal("0"));
        standardRate.setIncomeUpper(new BigDecimal("42000"));
        standardRate.setRate(new BigDecimal("0.20"));
        standardRate.setIsActive(true);
        standardRate.setEffectiveFrom(LocalDate.of(2025, 1, 1));

        TaxConfiguration higherRate = new TaxConfiguration();
        higherRate.setTaxYear(2025);
        higherRate.setTaxType(TaxType.PAYE);
        higherRate.setBandName("Higher Rate");
        higherRate.setIncomeLower(new BigDecimal("42000"));
        higherRate.setIncomeUpper(new BigDecimal("999999999"));
        higherRate.setRate(new BigDecimal("0.40"));
        higherRate.setIsActive(true);
        higherRate.setEffectiveFrom(LocalDate.of(2025, 1, 1));

        mockPayeBands = Arrays.asList(standardRate, higherRate);
    }

    @Test
    void testCalculatePaye_IncomeBelowStandardBand() {
        when(taxConfigRepository.findActiveTaxBands(eq(2025), eq(TaxType.PAYE)))
                .thenReturn(mockPayeBands);

        BigDecimal grossPay = new BigDecimal("30000");
        BigDecimal taxCredits = new BigDecimal("3300");

        // Expected: 30000 * 0.20 = 6000 - 3300 = 2700
        BigDecimal paye = payeService.calculatePaye(grossPay, taxCredits, 2025);

        assertEquals(new BigDecimal("2700.00"), paye);
    }

    @Test
    void testCalculatePaye_IncomeSpansBothBands() {
        when(taxConfigRepository.findActiveTaxBands(eq(2025), eq(TaxType.PAYE)))
                .thenReturn(mockPayeBands);

        BigDecimal grossPay = new BigDecimal("50000");
        BigDecimal taxCredits = new BigDecimal("3300");

        // Expected: (42000 * 0.20) + (8000 * 0.40) = 8400 + 3200 = 11600 - 3300 = 8300
        BigDecimal paye = payeService.calculatePaye(grossPay, taxCredits, 2025);

        assertEquals(new BigDecimal("8300.00"), paye);
    }

    @Test
    void testCalculatePaye_HighIncome() {
        when(taxConfigRepository.findActiveTaxBands(eq(2025), eq(TaxType.PAYE)))
                .thenReturn(mockPayeBands);

        BigDecimal grossPay = new BigDecimal("100000");
        BigDecimal taxCredits = new BigDecimal("3300");

        // Expected: (42000 * 0.20) + (58000 * 0.40) = 8400 + 23200 = 31600 - 3300 = 28300
        BigDecimal paye = payeService.calculatePaye(grossPay, taxCredits, 2025);

        assertEquals(new BigDecimal("28300.00"), paye);
    }

    @Test
    void testCalculatePaye_NoTaxCredits() {
        when(taxConfigRepository.findActiveTaxBands(eq(2025), eq(TaxType.PAYE)))
                .thenReturn(mockPayeBands);

        BigDecimal grossPay = new BigDecimal("30000");

        // Expected: 30000 * 0.20 = 6000
        BigDecimal paye = payeService.calculatePaye(grossPay, null, 2025);

        assertEquals(new BigDecimal("6000.00"), paye);
    }

    @Test
    void testCalculatePaye_TaxCreditsExceedTax() {
        when(taxConfigRepository.findActiveTaxBands(eq(2025), eq(TaxType.PAYE)))
                .thenReturn(mockPayeBands);

        BigDecimal grossPay = new BigDecimal("10000");
        BigDecimal taxCredits = new BigDecimal("5000");

        // Expected: 10000 * 0.20 = 2000 - 5000 = -3000, should be 0
        BigDecimal paye = payeService.calculatePaye(grossPay, taxCredits, 2025);

        assertEquals(0, paye.compareTo(BigDecimal.ZERO), "PAYE should be zero when credits exceed tax");
    }

    @Test
    void testCalculatePaye_ZeroIncome() {
        // Service returns early for zero income without calling repository
        BigDecimal paye = payeService.calculatePaye(BigDecimal.ZERO, BigDecimal.ZERO, 2025);

        assertEquals(0, paye.compareTo(BigDecimal.ZERO), "PAYE should be zero for zero income");
    }

    @Test
    void testCalculatePaye_NegativeIncome() {
        // Service returns early for negative income without calling repository
        BigDecimal paye = payeService.calculatePaye(new BigDecimal("-1000"), BigDecimal.ZERO, 2025);

        assertEquals(0, paye.compareTo(BigDecimal.ZERO), "PAYE should be zero for negative income");
    }
}
