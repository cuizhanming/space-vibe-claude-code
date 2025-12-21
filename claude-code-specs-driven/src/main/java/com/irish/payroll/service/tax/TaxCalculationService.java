package com.irish.payroll.service.tax;

import com.irish.payroll.dto.response.TaxCalculationResponse;
import com.irish.payroll.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Orchestrator service for calculating all Irish taxes.
 * Coordinates PAYE, PRSI, and USC calculations.
 */
@Service
public class TaxCalculationService {

    @Autowired
    private PayeCalculationService payeService;

    @Autowired
    private PrsiCalculationService prsiService;

    @Autowired
    private UscCalculationService uscService;

    /**
     * Calculate all taxes for an employee's pay period.
     *
     * @param employee Employee to calculate taxes for
     * @param grossPay Gross pay amount for the period
     * @param taxYear Tax year for rate lookup
     * @return Tax calculation response with all tax breakdowns
     */
    public TaxCalculationResponse calculateAllTaxes(Employee employee, BigDecimal grossPay, int taxYear) {
        if (grossPay == null || grossPay.compareTo(BigDecimal.ZERO) <= 0) {
            return new TaxCalculationResponse(
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
            );
        }

        // Calculate each tax component
        BigDecimal paye = payeService.calculatePaye(
                grossPay,
                employee.getTaxCreditsAnnual(),
                taxYear
        );

        BigDecimal prsi = prsiService.calculateEmployeePrsi(
                grossPay,
                employee.getPayFrequency()
        );

        BigDecimal usc = uscService.calculateUsc(grossPay, taxYear);

        // Calculate total deductions and net pay
        BigDecimal totalDeductions = paye.add(prsi).add(usc);
        BigDecimal netPay = grossPay.subtract(totalDeductions)
                .setScale(2, RoundingMode.HALF_UP);

        // Build response
        return new TaxCalculationResponse(
                grossPay,
                paye,
                prsi,
                usc,
                employee.getTaxCreditsAnnual(),
                netPay
        );
    }
}
