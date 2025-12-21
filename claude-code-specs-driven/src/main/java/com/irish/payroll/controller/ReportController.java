package com.irish.payroll.controller;

import com.irish.payroll.service.report.ExcelReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

/**
 * REST controller for report generation.
 */
@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reports", description = "Report generation endpoints")
public class ReportController {

    @Autowired
    private ExcelReportService excelReportService;

    @GetMapping("/payroll/{payrollId}/excel")
    @Operation(summary = "Download payroll Excel report", description = "Generate and download Excel report for payroll")
    public ResponseEntity<byte[]> downloadPayrollExcel(@PathVariable UUID payrollId) throws IOException {
        byte[] excelBytes = excelReportService.generatePayrollReport(payrollId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "payroll-report-" + payrollId + ".xlsx");
        headers.setContentLength(excelBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }
}
