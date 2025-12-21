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
 * Service for calculating Irish USC (Universal Social Charge).
 *
 * USC Rates (2025):
 * - Band 1: 0.5% on first €12,012
 * - Band 2: 2% on next €13,748 (€12,013 - €25,760)
 * - Band 3: 4% on next €44,284 (€25,761 - €70,044)
 * - Band 4: 8% on balance over €70,044
 */
@Service
public class UscCalculationService {

    @Autowired
    private TaxConfigurationRepository taxConfigRepository;

    /**
     * Calculate USC for a given gross pay amount.
     *
     * @param grossPay Gross pay amount for the period
     * @param taxYear Tax year for rate lookup
     * @return USC amount
     */
    public BigDecimal calculateUsc(BigDecimal grossPay, int taxYear) {
        if (grossPay == null || grossPay.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // Fetch USC bands from database
        List<TaxConfiguration> uscBands = taxConfigRepository.findActiveTaxBands(taxYear, TaxType.USC);

        if (uscBands.isEmpty()) {
            throw new TaxCalculationException("No USC tax configuration found for year " + taxYear);
        }

        // Calculate USC across all bands
        BigDecimal totalUsc = BigDecimal.ZERO;
        BigDecimal remainingIncome = grossPay;

        for (TaxConfiguration band : uscBands) {
            if (remainingIncome.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal bandWidth = band.getIncomeUpper().subtract(band.getIncomeLower());
            BigDecimal taxableInBand = remainingIncome.min(bandWidth);
            BigDecimal uscForBand = taxableInBand.multiply(band.getRate())
                    .setScale(2, RoundingMode.HALF_UP);

            totalUsc = totalUsc.add(uscForBand);
            remainingIncome = remainingIncome.subtract(taxableInBand);
        }

        return totalUsc.setScale(2, RoundingMode.HALF_UP);
    }
}
