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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit tests for USC Calculation Service.
 */
@ExtendWith(MockitoExtension.class)
class UscCalculationServiceTest {

    @Mock
    private TaxConfigurationRepository taxConfigRepository;

    @InjectMocks
    private UscCalculationService uscService;

    private List<TaxConfiguration> mockUscBands;

    @BeforeEach
    void setUp() {
        // Create mock USC tax bands for 2025
        TaxConfiguration band1 = new TaxConfiguration();
        band1.setTaxYear(2025);
        band1.setTaxType(TaxType.USC);
        band1.setBandName("Band 1");
        band1.setIncomeLower(new BigDecimal("0"));
        band1.setIncomeUpper(new BigDecimal("12012"));
        band1.setRate(new BigDecimal("0.005")); // 0.5%
        band1.setIsActive(true);
        band1.setEffectiveFrom(LocalDate.of(2025, 1, 1));

        TaxConfiguration band2 = new TaxConfiguration();
        band2.setTaxYear(2025);
        band2.setTaxType(TaxType.USC);
        band2.setBandName("Band 2");
        band2.setIncomeLower(new BigDecimal("12012"));
        band2.setIncomeUpper(new BigDecimal("25760"));
        band2.setRate(new BigDecimal("0.02")); // 2%
        band2.setIsActive(true);
        band2.setEffectiveFrom(LocalDate.of(2025, 1, 1));

        TaxConfiguration band3 = new TaxConfiguration();
        band3.setTaxYear(2025);
        band3.setTaxType(TaxType.USC);
        band3.setBandName("Band 3");
        band3.setIncomeLower(new BigDecimal("25760"));
        band3.setIncomeUpper(new BigDecimal("70044"));
        band3.setRate(new BigDecimal("0.04")); // 4%
        band3.setIsActive(true);
        band3.setEffectiveFrom(LocalDate.of(2025, 1, 1));

        TaxConfiguration band4 = new TaxConfiguration();
        band4.setTaxYear(2025);
        band4.setTaxType(TaxType.USC);
        band4.setBandName("Band 4");
        band4.setIncomeLower(new BigDecimal("70044"));
        band4.setIncomeUpper(new BigDecimal("999999999"));
        band4.setRate(new BigDecimal("0.08")); // 8%
        band4.setIsActive(true);
        band4.setEffectiveFrom(LocalDate.of(2025, 1, 1));

        mockUscBands = Arrays.asList(band1, band2, band3, band4);
    }

    @Test
    void testCalculateUsc_IncomeBand1Only() {
        when(taxConfigRepository.findActiveTaxBands(eq(2025), eq(TaxType.USC)))
                .thenReturn(mockUscBands);

        BigDecimal grossPay = new BigDecimal("10000");

        // Expected: 10000 * 0.005 = 50.00
        BigDecimal usc = uscService.calculateUsc(grossPay, 2025);

        assertEquals(new BigDecimal("50.00"), usc);
    }

    @Test
    void testCalculateUsc_IncomeBand1And2() {
        when(taxConfigRepository.findActiveTaxBands(eq(2025), eq(TaxType.USC)))
                .thenReturn(mockUscBands);

        BigDecimal grossPay = new BigDecimal("20000");

        // Expected: (12012 * 0.005) + (7988 * 0.02) = 60.06 + 159.76 = 219.82
        BigDecimal usc = uscService.calculateUsc(grossPay, 2025);

        assertEquals(new BigDecimal("219.82"), usc);
    }

    @Test
    void testCalculateUsc_IncomeSpansAllBands() {
        when(taxConfigRepository.findActiveTaxBands(eq(2025), eq(TaxType.USC)))
                .thenReturn(mockUscBands);

        BigDecimal grossPay = new BigDecimal("100000");

        // Expected:
        // Band 1: 12012 * 0.005 = 60.06
        // Band 2: 13748 * 0.02 = 274.96
        // Band 3: 44284 * 0.04 = 1771.36
        // Band 4: 29956 * 0.08 = 2396.48
        // Total: 4502.86
        BigDecimal usc = uscService.calculateUsc(grossPay, 2025);

        assertEquals(new BigDecimal("4502.86"), usc);
    }

    @Test
    void testCalculateUsc_AtBand1Boundary() {
        when(taxConfigRepository.findActiveTaxBands(eq(2025), eq(TaxType.USC)))
                .thenReturn(mockUscBands);

        BigDecimal grossPay = new BigDecimal("12012");

        // Expected: 12012 * 0.005 = 60.06
        BigDecimal usc = uscService.calculateUsc(grossPay, 2025);

        assertEquals(new BigDecimal("60.06"), usc);
    }

    @Test
    void testCalculateUsc_ZeroIncome() {
        // Service returns early for zero income without calling repository
        BigDecimal usc = uscService.calculateUsc(BigDecimal.ZERO, 2025);

        assertEquals(0, usc.compareTo(BigDecimal.ZERO), "USC should be zero for zero income");
    }

    @Test
    void testCalculateUsc_NullIncome() {
        // Service returns early for null income without calling repository
        BigDecimal usc = uscService.calculateUsc(null, 2025);

        assertEquals(0, usc.compareTo(BigDecimal.ZERO), "USC should be zero for null income");
    }
}
