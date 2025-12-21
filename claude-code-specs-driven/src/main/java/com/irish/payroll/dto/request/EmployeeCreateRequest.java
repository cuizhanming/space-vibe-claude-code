package com.irish.payroll.dto.request;

import com.irish.payroll.entity.PayFrequency;
import com.irish.payroll.validation.ValidPpsNumber;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for creating a new employee.
 */
public class EmployeeCreateRequest {

    @ValidPpsNumber
    private String ppsNumber;

    @NotBlank(message = "First name is required")
    @Size(max = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100)
    private String lastName;

    @Email(message = "Valid email is required")
    @NotBlank
    private String email;

    private LocalDate dateOfBirth;

    @NotNull(message = "Hire date is required")
    private LocalDate hireDate;

    @Size(max = 100)
    private String jobTitle;

    @Size(max = 100)
    private String department;

    @NotNull(message = "Gross salary is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Gross salary must be greater than 0")
    private BigDecimal grossSalary;

    @NotNull(message = "Pay frequency is required")
    private PayFrequency payFrequency;

    @Size(max = 20)
    private String bankAccountNumber;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal taxCreditsAnnual = BigDecimal.ZERO;

    // Getters and Setters

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

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public BigDecimal getTaxCreditsAnnual() {
        return taxCreditsAnnual;
    }

    public void setTaxCreditsAnnual(BigDecimal taxCreditsAnnual) {
        this.taxCreditsAnnual = taxCreditsAnnual;
    }
}
