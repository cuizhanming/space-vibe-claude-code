package com.irish.payroll.service.report;

import com.irish.payroll.entity.Payroll;
import com.irish.payroll.entity.Payslip;
import com.irish.payroll.service.PayrollService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Service for generating Excel reports.
 */
@Service
public class ExcelReportService {

    @Autowired
    private PayrollService payrollService;

    /**
     * Generate Excel report for a payroll.
     *
     * @param payrollId Payroll ID
     * @return Excel file as byte array
     */
    public byte[] generatePayrollReport(UUID payrollId) throws IOException {
        Payroll payroll = payrollService.getPayrollEntity(payrollId);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Payroll Report");

        // Create header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Create currency style
        CellStyle currencyStyle = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        currencyStyle.setDataFormat(format.getFormat("€#,##0.00"));

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "Employee Name", "PPS Number", "Gross Pay", "PAYE", "PRSI", "USC", "Net Pay",
                "YTD Gross", "YTD PAYE", "YTD PRSI", "YTD USC", "YTD Net"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Create data rows
        int rowNum = 1;
        for (Payslip payslip : payroll.getPayslips()) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(payslip.getEmployee().getFullName());
            row.createCell(1).setCellValue(payslip.getEmployee().getPpsNumber());

            Cell grossCell = row.createCell(2);
            grossCell.setCellValue(payslip.getGrossPay().doubleValue());
            grossCell.setCellStyle(currencyStyle);

            Cell payeCell = row.createCell(3);
            payeCell.setCellValue(payslip.getPayeDeduction().doubleValue());
            payeCell.setCellStyle(currencyStyle);

            Cell prsiCell = row.createCell(4);
            prsiCell.setCellValue(payslip.getPrsiDeduction().doubleValue());
            prsiCell.setCellStyle(currencyStyle);

            Cell uscCell = row.createCell(5);
            uscCell.setCellValue(payslip.getUscDeduction().doubleValue());
            uscCell.setCellStyle(currencyStyle);

            Cell netCell = row.createCell(6);
            netCell.setCellValue(payslip.getNetPay().doubleValue());
            netCell.setCellStyle(currencyStyle);

            Cell ytdGrossCell = row.createCell(7);
            ytdGrossCell.setCellValue(payslip.getYtdGross().doubleValue());
            ytdGrossCell.setCellStyle(currencyStyle);

            Cell ytdPayeCell = row.createCell(8);
            ytdPayeCell.setCellValue(payslip.getYtdPaye().doubleValue());
            ytdPayeCell.setCellStyle(currencyStyle);

            Cell ytdPrsiCell = row.createCell(9);
            ytdPrsiCell.setCellValue(payslip.getYtdPrsi().doubleValue());
            ytdPrsiCell.setCellStyle(currencyStyle);

            Cell ytdUscCell = row.createCell(10);
            ytdUscCell.setCellValue(payslip.getYtdUsc().doubleValue());
            ytdUscCell.setCellStyle(currencyStyle);

            Cell ytdNetCell = row.createCell(11);
            ytdNetCell.setCellValue(payslip.getYtdNet().doubleValue());
            ytdNetCell.setCellStyle(currencyStyle);
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }
}
