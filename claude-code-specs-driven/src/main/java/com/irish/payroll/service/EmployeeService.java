package com.irish.payroll.service;

import com.irish.payroll.dto.request.EmployeeCreateRequest;
import com.irish.payroll.dto.response.EmployeeResponse;
import com.irish.payroll.entity.Employee;
import com.irish.payroll.exception.EmployeeNotFoundException;
import com.irish.payroll.exception.PayrollProcessingException;
import com.irish.payroll.mapper.EmployeeMapper;
import com.irish.payroll.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing employees.
 */
@Service
@Transactional
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * Create a new employee.
     *
     * @param request Employee creation request
     * @return Created employee response
     */
    public EmployeeResponse createEmployee(EmployeeCreateRequest request) {
        // Validate PPS number uniqueness
        if (employeeRepository.findByPpsNumber(request.getPpsNumber()).isPresent()) {
            throw new PayrollProcessingException("Employee with PPS number " + request.getPpsNumber() + " already exists");
        }

        // Validate email uniqueness
        if (employeeRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new PayrollProcessingException("Employee with email " + request.getEmail() + " already exists");
        }

        Employee employee = employeeMapper.toEntity(request);
        employee = employeeRepository.save(employee);

        return employeeMapper.toResponse(employee);
    }

    /**
     * Get employee by ID.
     *
     * @param id Employee ID
     * @return Employee response
     */
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployee(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        return employeeMapper.toResponse(employee);
    }

    /**
     * Get employee entity by ID (for internal use).
     *
     * @param id Employee ID
     * @return Employee entity
     */
    @Transactional(readOnly = true)
    public Employee getEmployeeEntity(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    /**
     * Get all active employees.
     *
     * @return List of active employee responses
     */
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllActiveEmployees() {
        return employeeRepository.findByIsActiveTrue()
                .stream()
                .map(employeeMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all employees.
     *
     * @return List of all employee responses
     */
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(employeeMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Deactivate an employee.
     *
     * @param id Employee ID
     */
    public void deactivateEmployee(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        employee.setIsActive(false);
        employeeRepository.save(employee);
    }

    /**
     * Delete an employee permanently (use with caution).
     *
     * @param id Employee ID
     */
    public void deleteEmployee(UUID id) {
        if (!employeeRepository.existsById(id)) {
            throw new EmployeeNotFoundException(id);
        }

        employeeRepository.deleteById(id);
    }
}
