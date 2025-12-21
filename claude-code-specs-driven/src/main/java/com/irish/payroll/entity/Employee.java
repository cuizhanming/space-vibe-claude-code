package com.irish.payroll.entity;

import com.irish.payroll.validation.ValidPpsNumber;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing an employee in the Irish payroll system.
 */
@Entity
@Table(name = "employees")
public class Employee extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ValidPpsNumber
    @Column(name = "pps_number", unique = true, nullable = false, length = 10)
    private String ppsNumber;

    @NotBlank
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotBlank
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Email
    @NotBlank
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @NotNull
    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Column(name = "job_title", length = 100)
    private String jobTitle;

    @Column(name = "department", length = 100)
    private String department;

    @NotNull
    @Column(name = "gross_salary", nullable = false, precision = 10, scale = 2)
    private BigDecimal grossSalary;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "pay_frequency", nullable = false, length = 20)
    private PayFrequency payFrequency;

    @Column(name = "bank_account_number", length = 20)
    private String bankAccountNumber;

    @Column(name = "tax_credits_annual", precision = 10, scale = 2)
    private BigDecimal taxCreditsAnnual = BigDecimal.ZERO;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Payslip> payslips = new ArrayList<>();

    // Constructors

    public Employee() {
    }

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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public List<Payslip> getPayslips() {
        return payslips;
    }

    public void setPayslips(List<Payslip> payslips) {
        this.payslips = payslips;
    }

    /**
     * Helper method to get employee's full name.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
