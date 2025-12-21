package com.irish.payroll.service;

import com.irish.payroll.dto.request.PayrollRunRequest;
import com.irish.payroll.dto.response.PayrollResponse;
import com.irish.payroll.dto.response.TaxCalculationResponse;
import com.irish.payroll.entity.Employee;
import com.irish.payroll.entity.Payroll;
import com.irish.payroll.entity.PayrollStatus;
import com.irish.payroll.entity.Payslip;
import com.irish.payroll.exception.PayrollProcessingException;
import com.irish.payroll.mapper.PayrollMapper;
import com.irish.payroll.repository.EmployeeRepository;
import com.irish.payroll.repository.PayrollRepository;
import com.irish.payroll.repository.PayslipRepository;
import com.irish.payroll.service.tax.TaxCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for processing payroll runs.
 */
@Service
@Transactional
public class PayrollService {

    @Autowired
    private PayrollRepository payrollRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PayslipRepository payslipRepository;

    @Autowired
    private TaxCalculationService taxCalculationService;

    @Autowired
    private PayrollMapper payrollMapper;

    /**
     * Process payroll for a given period.
     *
     * @param request Payroll run request
     * @return Payroll response with all payslips
     */
    public PayrollResponse processPayroll(PayrollRunRequest request) {
        // Validate period doesn't already exist
        if (payrollRepository.findByPayPeriodStartAndPayPeriodEnd(
                request.getPayPeriodStart(), request.getPayPeriodEnd()).isPresent()) {
            throw new PayrollProcessingException(
                    "Payroll already exists for period " + request.getPayPeriodStart() + " to " + request.getPayPeriodEnd()
            );
        }

        // Validate dates
        if (request.getPayPeriodStart().isAfter(request.getPayPeriodEnd())) {
            throw new PayrollProcessingException("Pay period start date must be before end date");
        }

        // Create payroll
        Payroll payroll = new Payroll();
        payroll.setPayPeriodStart(request.getPayPeriodStart());
        payroll.setPayPeriodEnd(request.getPayPeriodEnd());
        payroll.setPaymentDate(request.getPaymentDate());
        payroll.setStatus(PayrollStatus.DRAFT);

        // Get active employees
        List<Employee> employees = employeeRepository.findByIsActiveTrue();

        if (employees.isEmpty()) {
            throw new PayrollProcessingException("No active employees found to process payroll");
        }

        int currentYear = request.getPayPeriodEnd().getYear();
        List<Payslip> payslips = new ArrayList<>();

        // Process each employee
        for (Employee employee : employees) {
            // Use employee's gross salary as the gross pay for this period
            BigDecimal grossPay = employee.getGrossSalary();

            // Calculate taxes
            TaxCalculationResponse taxCalc = taxCalculationService
                    .calculateAllTaxes(employee, grossPay, currentYear);

            // Calculate YTD amounts
            BigDecimal ytdGross = payslipRepository.calculateYtdGross(employee.getId(), currentYear)
                    .add(taxCalc.getGrossPay());
            BigDecimal ytdPaye = payslipRepository.calculateYtdPaye(employee.getId(), currentYear)
                    .add(taxCalc.getPaye());
            BigDecimal ytdPrsi = payslipRepository.calculateYtdPrsi(employee.getId(), currentYear)
                    .add(taxCalc.getPrsi());
            BigDecimal ytdUsc = payslipRepository.calculateYtdUsc(employee.getId(), currentYear)
                    .add(taxCalc.getUsc());
            BigDecimal ytdNet = ytdGross.subtract(ytdPaye).subtract(ytdPrsi).subtract(ytdUsc);

            // Create payslip
            Payslip payslip = new Payslip();
            payslip.setPayroll(payroll);
            payslip.setEmployee(employee);
            payslip.setGrossPay(taxCalc.getGrossPay());
            payslip.setPayeDeduction(taxCalc.getPaye());
            payslip.setPrsiDeduction(taxCalc.getPrsi());
            payslip.setUscDeduction(taxCalc.getUsc());
            payslip.setNetPay(taxCalc.getNetPay());
            payslip.setTaxCreditsUsed(employee.getTaxCreditsAnnual());
            payslip.setYtdGross(ytdGross);
            payslip.setYtdPaye(ytdPaye);
            payslip.setYtdPrsi(ytdPrsi);
            payslip.setYtdUsc(ytdUsc);
            payslip.setYtdNet(ytdNet);

            payslips.add(payslip);
        }

        // Set payslips on payroll
        payroll.setPayslips(payslips);

        // Calculate totals
        payroll.calculateTotals();

        // Mark as processed
        payroll.setStatus(PayrollStatus.PROCESSED);
        payroll.setProcessedDate(LocalDateTime.now());

        // Save payroll (cascades to payslips)
        payroll = payrollRepository.save(payroll);

        return payrollMapper.toResponse(payroll);
    }

    /**
     * Get payroll by ID.
     *
     * @param id Payroll ID
     * @return Payroll response
     */
    @Transactional(readOnly = true)
    public PayrollResponse getPayroll(UUID id) {
        Payroll payroll = payrollRepository.findById(id)
                .orElseThrow(() -> new PayrollProcessingException("Payroll not found with id: " + id));

        return payrollMapper.toResponse(payroll);
    }

    /**
     * Get all payrolls ordered by date descending.
     *
     * @return List of payroll responses
     */
    @Transactional(readOnly = true)
    public List<PayrollResponse> getAllPayrolls() {
        return payrollRepository.findAllByOrderByPayPeriodEndDesc()
                .stream()
                .map(payrollMapper::toResponse)
                .toList();
    }

    /**
     * Get payroll entity by ID (for internal use).
     *
     * @param id Payroll ID
     * @return Payroll entity
     */
    @Transactional(readOnly = true)
    public Payroll getPayrollEntity(UUID id) {
        return payrollRepository.findById(id)
                .orElseThrow(() -> new PayrollProcessingException("Payroll not found with id: " + id));
    }
}
