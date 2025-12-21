package com.irish.payroll.service.tax;

import com.irish.payroll.entity.PayFrequency;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Service for calculating Irish PRSI (Pay Related Social Insurance).
 *
 * PRSI Rates (2025):
 * - Employee PRSI (Class A): 4% of gross pay
 * - Exemption Thresholds:
 *   - Weekly: €352
 *   - Monthly: €1,526
 * - No PRSI charged if income is below the threshold
 */
@Service
public class PrsiCalculationService {

    private static final BigDecimal EMPLOYEE_PRSI_RATE = new BigDecimal("0.04");
    private static final BigDecimal WEEKLY_THRESHOLD = new BigDecimal("352");
    private static final BigDecimal MONTHLY_THRESHOLD = new BigDecimal("1526");

    /**
     * Calculate employee PRSI for a given gross pay amount.
     *
     * @param grossPay Gross pay amount for the period
     * @param frequency Pay frequency (weekly or monthly)
     * @return PRSI amount
     */
    public BigDecimal calculateEmployeePrsi(BigDecimal grossPay, PayFrequency frequency) {
        if (grossPay == null || grossPay.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // Determine threshold based on pay frequency
        BigDecimal threshold = frequency == PayFrequency.WEEKLY ? WEEKLY_THRESHOLD : MONTHLY_THRESHOLD;

        // No PRSI if below threshold
        if (grossPay.compareTo(threshold) < 0) {
            return BigDecimal.ZERO;
        }

        // Calculate 4% of gross pay
        return grossPay.multiply(EMPLOYEE_PRSI_RATE)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
