package com.irish.payroll.controller;

import com.irish.payroll.dto.request.PayrollRunRequest;
import com.irish.payroll.dto.response.PayrollResponse;
import com.irish.payroll.service.PayrollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for payroll processing.
 */
@RestController
@RequestMapping("/api/payrolls")
@Tag(name = "Payroll", description = "Payroll processing endpoints")
public class PayrollController {

    @Autowired
    private PayrollService payrollService;

    @PostMapping("/process")
    @Operation(summary = "Process payroll", description = "Process payroll for a period")
    public ResponseEntity<PayrollResponse> processPayroll(@Valid @RequestBody PayrollRunRequest request) {
        PayrollResponse response = payrollService.processPayroll(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payroll by ID", description = "Get payroll details with all payslips")
    public ResponseEntity<PayrollResponse> getPayroll(@PathVariable UUID id) {
        PayrollResponse payroll = payrollService.getPayroll(id);
        return ResponseEntity.ok(payroll);
    }

    @GetMapping
    @Operation(summary = "Get all payrolls", description = "Get all payrolls ordered by date")
    public ResponseEntity<List<PayrollResponse>> getAllPayrolls() {
        List<PayrollResponse> payrolls = payrollService.getAllPayrolls();
        return ResponseEntity.ok(payrolls);
    }
}
