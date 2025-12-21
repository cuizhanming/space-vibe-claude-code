package com.irish.payroll.dto.response;

import java.math.BigDecimal;

/**
 * DTO for tax calculation response.
 */
public class TaxCalculationResponse {

    private BigDecimal grossPay;
    private BigDecimal paye;
    private BigDecimal prsi;
    private BigDecimal usc;
    private BigDecimal taxCreditsUsed;
    private BigDecimal netPay;

    // Constructors

    public TaxCalculationResponse() {
    }

    public TaxCalculationResponse(BigDecimal grossPay, BigDecimal paye, BigDecimal prsi,
                                   BigDecimal usc, BigDecimal taxCreditsUsed, BigDecimal netPay) {
        this.grossPay = grossPay;
        this.paye = paye;
        this.prsi = prsi;
        this.usc = usc;
        this.taxCreditsUsed = taxCreditsUsed;
        this.netPay = netPay;
    }

    // Getters and Setters

    public BigDecimal getGrossPay() {
        return grossPay;
    }

    public void setGrossPay(BigDecimal grossPay) {
        this.grossPay = grossPay;
    }

    public BigDecimal getPaye() {
        return paye;
    }

    public void setPaye(BigDecimal paye) {
        this.paye = paye;
    }

    public BigDecimal getPrsi() {
        return prsi;
    }

    public void setPrsi(BigDecimal prsi) {
        this.prsi = prsi;
    }

    public BigDecimal getUsc() {
        return usc;
    }

    public void setUsc(BigDecimal usc) {
        this.usc = usc;
    }

    public BigDecimal getTaxCreditsUsed() {
        return taxCreditsUsed;
    }

    public void setTaxCreditsUsed(BigDecimal taxCreditsUsed) {
        this.taxCreditsUsed = taxCreditsUsed;
    }

    public BigDecimal getNetPay() {
        return netPay;
    }

    public void setNetPay(BigDecimal netPay) {
        this.netPay = netPay;
    }
}
