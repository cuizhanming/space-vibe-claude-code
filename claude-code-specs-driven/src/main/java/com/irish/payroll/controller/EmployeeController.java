package com.irish.payroll.controller;

import com.irish.payroll.dto.request.EmployeeCreateRequest;
import com.irish.payroll.dto.response.EmployeeResponse;
import com.irish.payroll.service.EmployeeService;
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
 * REST controller for employee management.
 */
@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employees", description = "Employee management endpoints")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping
    @Operation(summary = "Create employee", description = "Create a new employee")
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeCreateRequest request) {
        EmployeeResponse response = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all employees", description = "Get all active employees")
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        List<EmployeeResponse> employees = employeeService.getAllActiveEmployees();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID", description = "Get employee details by ID")
    public ResponseEntity<EmployeeResponse> getEmployee(@PathVariable UUID id) {
        EmployeeResponse employee = employeeService.getEmployee(id);
        return ResponseEntity.ok(employee);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate employee", description = "Deactivate an employee")
    public ResponseEntity<Void> deactivateEmployee(@PathVariable UUID id) {
        employeeService.deactivateEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
