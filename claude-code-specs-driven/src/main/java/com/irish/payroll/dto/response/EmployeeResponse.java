package com.irish.payroll.dto.response;

import com.irish.payroll.entity.PayFrequency;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for employee response.
 */
public class EmployeeResponse {

    private UUID id;
    private String ppsNumber;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate dateOfBirth;
    private LocalDate hireDate;
    private String jobTitle;
    private String department;
    private BigDecimal grossSalary;
    private PayFrequency payFrequency;
    private BigDecimal taxCreditsAnnual;
    private Boolean isActive;

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPpsNumber() {
        return ppsNumber;
    }

    public void setPpsNumber(String ppsNumber) {
        this.ppsNumber = ppsNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public BigDecimal getGrossSalary() {
        return grossSalary;
    }

    public void setGrossSalary(BigDecimal grossSalary) {
        this.grossSalary = grossSalary;
    }

    public PayFrequency getPayFrequency() {
        return payFrequency;
    }

    public void setPayFrequency(PayFrequency payFrequency) {
        this.payFrequency = payFrequency;
    }

    public BigDecimal getTaxCreditsAnnual() {
        return taxCreditsAnnual;
    }

    public void setTaxCreditsAnnual(BigDecimal taxCreditsAnnual) {
        this.taxCreditsAnnual = taxCreditsAnnual;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}
