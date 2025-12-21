package com.irish.payroll.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entity representing an individual employee's payslip within a payroll run.
 */
@Entity
@Table(name = "payslips")
public class Payslip extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payroll_id", nullable = false)
    private Payroll payroll;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @NotNull
    @Column(name = "gross_pay", nullable = false, precision = 10, scale = 2)
    private BigDecimal grossPay;

    @Column(name = "paye_deduction", precision = 10, scale = 2)
    private BigDecimal payeDeduction = BigDecimal.ZERO;

    @Column(name = "prsi_deduction", precision = 10, scale = 2)
    private BigDecimal prsiDeduction = BigDecimal.ZERO;

    @Column(name = "usc_deduction", precision = 10, scale = 2)
    private BigDecimal uscDeduction = BigDecimal.ZERO;

    @NotNull
    @Column(name = "net_pay", nullable = false, precision = 10, scale = 2)
    private BigDecimal netPay;

    @Column(name = "tax_credits_used", precision = 10, scale = 2)
    private BigDecimal taxCreditsUsed = BigDecimal.ZERO;

    @Column(name = "ytd_gross", precision = 12, scale = 2)
    private BigDecimal ytdGross;

    @Column(name = "ytd_paye", precision = 12, scale = 2)
    private BigDecimal ytdPaye;

    @Column(name = "ytd_prsi", precision = 12, scale = 2)
    private BigDecimal ytdPrsi;

    @Column(name = "ytd_usc", precision = 12, scale = 2)
    private BigDecimal ytdUsc;

    @Column(name = "ytd_net", precision = 12, scale = 2)
    private BigDecimal ytdNet;

    // Constructors

    public Payslip() {
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Payroll getPayroll() {
        return payroll;
    }

    public void setPayroll(Payroll payroll) {
        this.payroll = payroll;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
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
