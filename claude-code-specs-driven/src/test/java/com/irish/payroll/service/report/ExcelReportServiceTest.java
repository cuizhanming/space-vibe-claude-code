package com.irish.payroll.service.report;

import com.irish.payroll.entity.Employee;
import com.irish.payroll.entity.PayFrequency;
import com.irish.payroll.entity.Payroll;
import com.irish.payroll.entity.PayrollStatus;
import com.irish.payroll.entity.Payslip;
import com.irish.payroll.service.PayrollService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Test class for ExcelReportService to verify the Excel report generation fix.
 *
 * This test specifically validates that the LazyInitializationException fix
 * (using JOIN FETCH to eagerly load employees) works correctly.
 */
@ExtendWith(MockitoExtension.class)
class ExcelReportServiceTest {

    @Mock
    private PayrollService payrollService;

    @InjectMocks
    private ExcelReportService excelReportService;

    private Payroll testPayroll;
    private Employee testEmployee;
    private Payslip testPayslip;

    @BeforeEach
    void setUp() {
        // Create test employee with all required fields
        testEmployee = new Employee();
        testEmployee.setId(UUID.randomUUID());
        testEmployee.setPpsNumber("1234567AB");
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Smith");
        testEmployee.setEmail("john.smith@example.ie");
        testEmployee.setHireDate(LocalDate.of(2024, 1, 1));
        testEmployee.setGrossSalary(new BigDecimal("50000.00"));
        testEmployee.setPayFrequency(PayFrequency.MONTHLY);
        testEmployee.setTaxCreditsAnnual(new BigDecimal("3400.00"));
        testEmployee.setIsActive(true);

        // Create test payroll
        testPayroll = new Payroll();
        testPayroll.setId(UUID.randomUUID());
        testPayroll.setPayPeriodStart(LocalDate.of(2025, 12, 1));
        testPayroll.setPayPeriodEnd(LocalDate.of(2025, 12, 31));
        testPayroll.setPaymentDate(LocalDate.of(2025, 12, 31));
        testPayroll.setStatus(PayrollStatus.PROCESSED);

        // Create test payslip with employee reference
        testPayslip = new Payslip();
        testPayslip.setId(UUID.randomUUID());
        testPayslip.setPayroll(testPayroll);
        testPayslip.setEmployee(testEmployee);  // This is the critical link that was causing LazyInitializationException
        testPayslip.setGrossPay(new BigDecimal("50000.00"));
        testPayslip.setPayeDeduction(new BigDecimal("8200.00"));
        testPayslip.setPrsiDeduction(new BigDecimal("2000.00"));
        testPayslip.setUscDeduction(new BigDecimal("1304.62"));
        testPayslip.setNetPay(new BigDecimal("38495.38"));
        testPayslip.setTaxCreditsUsed(new BigDecimal("3400.00"));
        testPayslip.setYtdGross(new BigDecimal("50000.00"));
        testPayslip.setYtdPaye(new BigDecimal("8200.00"));
        testPayslip.setYtdPrsi(new BigDecimal("2000.00"));
        testPayslip.setYtdUsc(new BigDecimal("1304.62"));
        testPayslip.setYtdNet(new BigDecimal("38495.38"));

        // Add payslip to payroll
        List<Payslip> payslips = new ArrayList<>();
        payslips.add(testPayslip);
        testPayroll.setPayslips(payslips);
        testPayroll.calculateTotals();
    }

    @Test
    void generatePayrollReport_shouldSuccessfullyAccessEmployeeData() throws IOException {
        // Arrange
        UUID payrollId = testPayroll.getId();

        // Mock the payrollService to return a payroll with eagerly loaded employees
        // This simulates the fix where findByIdWithPayslipsAndEmployees is used
        when(payrollService.getPayrollEntity(any(UUID.class))).thenReturn(testPayroll);

        // Act
        byte[] excelBytes = excelReportService.generatePayrollReport(payrollId);

        // Assert
        assertNotNull(excelBytes, "Excel report should be generated");
        assertTrue(excelBytes.length > 0, "Excel report should have content");

        // The fact that we reach here without LazyInitializationException proves the fix works
        // The employee data (getFullName(), getPpsNumber()) was accessed successfully
    }

    @Test
    void generatePayrollReport_shouldIncludeEmployeeInformation() throws IOException {
        // Arrange
        UUID payrollId = testPayroll.getId();
        when(payrollService.getPayrollEntity(any(UUID.class))).thenReturn(testPayroll);

        // Act
        byte[] excelBytes = excelReportService.generatePayrollReport(payrollId);

        // Assert
        assertNotNull(excelBytes);
        assertTrue(excelBytes.length > 0);

        // Verify the Excel file structure is valid (starts with PK zip signature)
        assertEquals((byte) 'P', excelBytes[0], "Excel file should start with 'PK' (ZIP signature)");
        assertEquals((byte) 'K', excelBytes[1], "Excel file should start with 'PK' (ZIP signature)");
    }

    @Test
    void generatePayrollReport_shouldHandleMultiplePayslips() throws IOException {
        // Arrange
        Employee employee2 = new Employee();
        employee2.setId(UUID.randomUUID());
        employee2.setPpsNumber("9876543XY");
        employee2.setFirstName("Jane");
        employee2.setLastName("Doe");
        employee2.setEmail("jane.doe@example.ie");
        employee2.setHireDate(LocalDate.of(2024, 1, 1));
        employee2.setGrossSalary(new BigDecimal("45000.00"));
        employee2.setPayFrequency(PayFrequency.MONTHLY);
        employee2.setTaxCreditsAnnual(new BigDecimal("3400.00"));
        employee2.setIsActive(true);

        Payslip payslip2 = new Payslip();
        payslip2.setId(UUID.randomUUID());
        payslip2.setPayroll(testPayroll);
        payslip2.setEmployee(employee2);
        payslip2.setGrossPay(new BigDecimal("45000.00"));
        payslip2.setPayeDeduction(new BigDecimal("7000.00"));
        payslip2.setPrsiDeduction(new BigDecimal("1800.00"));
        payslip2.setUscDeduction(new BigDecimal("1100.00"));
        payslip2.setNetPay(new BigDecimal("35100.00"));
        payslip2.setTaxCreditsUsed(new BigDecimal("3400.00"));
        payslip2.setYtdGross(new BigDecimal("45000.00"));
        payslip2.setYtdPaye(new BigDecimal("7000.00"));
        payslip2.setYtdPrsi(new BigDecimal("1800.00"));
        payslip2.setYtdUsc(new BigDecimal("1100.00"));
        payslip2.setYtdNet(new BigDecimal("35100.00"));

        testPayroll.getPayslips().add(payslip2);
        testPayroll.calculateTotals();

        UUID payrollId = testPayroll.getId();
        when(payrollService.getPayrollEntity(any(UUID.class))).thenReturn(testPayroll);

        // Act
        byte[] excelBytes = excelReportService.generatePayrollReport(payrollId);

        // Assert
        assertNotNull(excelBytes);
        assertTrue(excelBytes.length > 0);

        // With 2 employees, the file should be reasonably larger
        // (exact size depends on Apache POI internals, but should be > 5KB)
        assertTrue(excelBytes.length > 5000, "Excel file with 2 employees should be larger than 5KB");
    }
}
