package com.irish.payroll.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a payroll run for a specific period.
 */
@Entity
@Table(name = "payrolls")
public class Payroll extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(name = "pay_period_start", nullable = false)
    private LocalDate payPeriodStart;

    @NotNull
    @Column(name = "pay_period_end", nullable = false)
    private LocalDate payPeriodEnd;

    @NotNull
    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PayrollStatus status;

    @Column(name = "total_gross", precision = 12, scale = 2)
    private BigDecimal totalGross;

    @Column(name = "total_paye", precision = 12, scale = 2)
    private BigDecimal totalPaye;

    @Column(name = "total_prsi", precision = 12, scale = 2)
    private BigDecimal totalPrsi;

    @Column(name = "total_usc", precision = 12, scale = 2)
    private BigDecimal totalUsc;

    @Column(name = "total_net", precision = 12, scale = 2)
    private BigDecimal totalNet;

    @Column(name = "processed_date")
    private LocalDateTime processedDate;

    @OneToMany(mappedBy = "payroll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payslip> payslips = new ArrayList<>();

    // Constructors

    public Payroll() {
        this.status = PayrollStatus.DRAFT;
    }

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

    public LocalDateTime getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(LocalDateTime processedDate) {
        this.processedDate = processedDate;
    }

    public List<Payslip> getPayslips() {
        return payslips;
    }

    public void setPayslips(List<Payslip> payslips) {
        this.payslips = payslips;
    }

    /**
     * Calculate total amounts from all payslips.
     */
    public void calculateTotals() {
        this.totalGross = payslips.stream()
                .map(Payslip::getGrossPay)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.totalPaye = payslips.stream()
                .map(Payslip::getPayeDeduction)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.totalPrsi = payslips.stream()
                .map(Payslip::getPrsiDeduction)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.totalUsc = payslips.stream()
                .map(Payslip::getUscDeduction)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.totalNet = payslips.stream()
                .map(Payslip::getNetPay)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
