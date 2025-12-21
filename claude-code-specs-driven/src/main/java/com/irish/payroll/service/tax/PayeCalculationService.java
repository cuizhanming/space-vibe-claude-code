package com.irish.payroll.service.tax;

import com.irish.payroll.entity.TaxConfiguration;
import com.irish.payroll.entity.TaxType;
import com.irish.payroll.exception.TaxCalculationException;
import com.irish.payroll.repository.TaxConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Service for calculating Irish PAYE (Pay As You Earn) tax.
 *
 * PAYE Tax Rates (2025):
 * - Standard Rate (20%): First €42,000
 * - Higher Rate (40%): Balance over €42,000
 * - Tax credits reduce the calculated tax amount
 */
@Service
public class PayeCalculationService {

    @Autowired
    private TaxConfigurationRepository taxConfigRepository;

    /**
     * Calculate PAYE tax for a given gross pay amount.
     *
     * @param grossPay Gross pay amount for the period
     * @param annualTaxCredits Annual tax credits
     * @param taxYear Tax year for rate lookup
     * @return PAYE tax amount
     */
    public BigDecimal calculatePaye(BigDecimal grossPay, BigDecimal annualTaxCredits, int taxYear) {
        if (grossPay == null || grossPay.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // Fetch PAYE bands from database
        List<TaxConfiguration> payeBands = taxConfigRepository.findActiveTaxBands(taxYear, TaxType.PAYE);

        if (payeBands.isEmpty()) {
            throw new TaxCalculationException("No PAYE tax configuration found for year " + taxYear);
        }

        // Calculate tax per band
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal remainingIncome = grossPay;

        for (TaxConfiguration band : payeBands) {
            if (remainingIncome.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal bandWidth = band.getIncomeUpper().subtract(band.getIncomeLower());
            BigDecimal taxableInBand = remainingIncome.min(bandWidth);
            BigDecimal taxForBand = taxableInBand.multiply(band.getRate())
                    .setScale(2, RoundingMode.HALF_UP);

            totalTax = totalTax.add(taxForBand);
            remainingIncome = remainingIncome.subtract(taxableInBand);
        }

        // Apply tax credits (if provided)
        if (annualTaxCredits != null && annualTaxCredits.compareTo(BigDecimal.ZERO) > 0) {
            totalTax = totalTax.subtract(annualTaxCredits);
        }

        // Tax cannot be negative
        return totalTax.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }
}
