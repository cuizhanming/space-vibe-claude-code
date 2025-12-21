package com.irish.payroll.dto.response;

import com.irish.payroll.entity.PayrollStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DTO for payroll response.
 */
public class PayrollResponse {

    private UUID id;
    private LocalDate payPeriodStart;
    private LocalDate payPeriodEnd;
    private LocalDate paymentDate;
    private PayrollStatus status;
    private BigDecimal totalGross;
    private BigDecimal totalPaye;
    private BigDecimal totalPrsi;
    private BigDecimal totalUsc;
    private BigDecimal totalNet;
    private List<PayslipResponse> payslips = new ArrayList<>();

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDate getPayPeriodStart() {
        return payPeriodStart;
    }

    public void setPayPeriodStart(LocalDate payPeriodStart) {
        this.payPeriodStart = payPeriodStart;
    }

    public LocalDate getPayPeriodEnd() {
        return payPeriodEnd;
    }

    public void setPayPeriodEnd(LocalDate payPeriodEnd) {
        this.payPeriodEnd = payPeriodEnd;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public PayrollStatus getStatus() {
        return status;
    }

    public void setStatus(PayrollStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalGross() {
        return totalGross;
    }

    public void setTotalGross(BigDecimal totalGross) {
        this.totalGross = totalGross;
    }

    public BigDecimal getTotalPaye() {
        return totalPaye;
    }

    public void setTotalPaye(BigDecimal totalPaye) {
        this.totalPaye = totalPaye;
    }

    public BigDecimal getTotalPrsi() {
        return totalPrsi;
    }

    public void setTotalPrsi(BigDecimal totalPrsi) {
        this.totalPrsi = totalPrsi;
    }

    public BigDecimal getTotalUsc() {
        return totalUsc;
    }

    public void setTotalUsc(BigDecimal totalUsc) {
        this.totalUsc = totalUsc;
    }

    public BigDecimal getTotalNet() {
        return totalNet;
    }

    public void setTotalNet(BigDecimal totalNet) {
        this.totalNet = totalNet;
    }

    public List<PayslipResponse> getPayslips() {
        return payslips;
    }

    public void setPayslips(List<PayslipResponse> payslips) {
        this.payslips = payslips;
    }
}
