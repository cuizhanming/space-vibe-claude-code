package com.irish.payroll.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for payslip response.
 */
public class PayslipResponse {

    private UUID id;
    private UUID payrollId;
    private UUID employeeId;
    private String employeeName;
    private String employeePpsNumber;
    private BigDecimal grossPay;
    private BigDecimal payeDeduction;
    private BigDecimal prsiDeduction;
    private BigDecimal uscDeduction;
    private BigDecimal netPay;
    private BigDecimal taxCreditsUsed;
    private BigDecimal ytdGross;
    private BigDecimal ytdPaye;
    private BigDecimal ytdPrsi;
    private BigDecimal ytdUsc;
    private BigDecimal ytdNet;

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getPayrollId() {
        return payrollId;
    }

    public void setPayrollId(UUID payrollId) {
        this.payrollId = payrollId;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(UUID employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeePpsNumber() {
        return employeePpsNumber;
    }

    public void setEmployeePpsNumber(String employeePpsNumber) {
        this.employeePpsNumber = employeePpsNumber;
    }

    public BigDecimal getGrossPay() {
        return grossPay;
    }

    public void setGrossPay(BigDecimal grossPay) {
        this.grossPay = grossPay;
    }

    public BigDecimal getPayeDeduction() {
        return payeDeduction;
    }

    public void setPayeDeduction(BigDecimal payeDeduction) {
        this.payeDeduction = payeDeduction;
    }

    public BigDecimal getPrsiDeduction() {
        return prsiDeduction;
    }

    public void setPrsiDeduction(BigDecimal prsiDeduction) {
        this.prsiDeduction = prsiDeduction;
    }

    public BigDecimal getUscDeduction() {
        return uscDeduction;
    }

    public void setUscDeduction(BigDecimal uscDeduction) {
        this.uscDeduction = uscDeduction;
    }

    public BigDecimal getNetPay() {
        return netPay;
    }

    public void setNetPay(BigDecimal netPay) {
        this.netPay = netPay;
    }

    public BigDecimal getTaxCreditsUsed() {
        return taxCreditsUsed;
    }

    public void setTaxCreditsUsed(BigDecimal taxCreditsUsed) {
        this.taxCreditsUsed = taxCreditsUsed;
    }

    public BigDecimal getYtdGross() {
        return ytdGross;
    }

    public void setYtdGross(BigDecimal ytdGross) {
        this.ytdGross = ytdGross;
    }

    public BigDecimal getYtdPaye() {
        return ytdPaye;
    }

    public void setYtdPaye(BigDecimal ytdPaye) {
        this.ytdPaye = ytdPaye;
    }

    public BigDecimal getYtdPrsi() {
        return ytdPrsi;
    }

    public void setYtdPrsi(BigDecimal ytdPrsi) {
        this.ytdPrsi = ytdPrsi;
    }

    public BigDecimal getYtdUsc() {
        return ytdUsc;
    }

    public void setYtdUsc(BigDecimal ytdUsc) {
        this.ytdUsc = ytdUsc;
    }

    public BigDecimal getYtdNet() {
        return ytdNet;
    }

    public void setYtdNet(BigDecimal ytdNet) {
        this.ytdNet = ytdNet;
    }
}
