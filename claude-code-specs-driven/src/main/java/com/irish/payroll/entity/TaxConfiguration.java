package com.irish.payroll.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Entity representing Irish tax configuration (rates and bands) for a specific year.
 */
@Entity
@Table(name = "tax_configurations")
public class TaxConfiguration extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(name = "tax_year", nullable = false)
    private Integer taxYear;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tax_type", nullable = false, length = 20)
    private TaxType taxType;

    @Column(name = "band_name", length = 50)
    private String bandName;

    @Column(name = "income_lower", precision = 15, scale = 2)
    private BigDecimal incomeLower;

    @Column(name = "income_upper", precision = 15, scale = 2)
    private BigDecimal incomeUpper;

    @NotNull
    @Column(name = "rate", nullable = false, precision = 5, scale = 4)
    private BigDecimal rate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @NotNull
    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    // Constructors

    public TaxConfiguration() {
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getTaxYear() {
        return taxYear;
    }

    public void setTaxYear(Integer taxYear) {
        this.taxYear = taxYear;
    }

    public TaxType getTaxType() {
        return taxType;
    }

    public void setTaxType(TaxType taxType) {
        this.taxType = taxType;
    }

    public String getBandName() {
        return bandName;
    }

    public void setBandName(String bandName) {
        this.bandName = bandName;
    }

    public BigDecimal getIncomeLower() {
        return incomeLower;
    }

    public void setIncomeLower(BigDecimal incomeLower) {
        this.incomeLower = incomeLower;
    }

    public BigDecimal getIncomeUpper() {
        return incomeUpper;
    }

    public void setIncomeUpper(BigDecimal incomeUpper) {
        this.incomeUpper = incomeUpper;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(LocalDate effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public LocalDate getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(LocalDate effectiveTo) {
        this.effectiveTo = effectiveTo;
    }
}
