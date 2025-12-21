package com.irish.payroll.repository;

import com.irish.payroll.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Employee entity.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    /**
     * Find employee by PPS number.
     */
    Optional<Employee> findByPpsNumber(String ppsNumber);

    /**
     * Find employee by email.
     */
    Optional<Employee> findByEmail(String email);

    /**
     * Find all active employees.
     */
    List<Employee> findByIsActiveTrue();

    /**
     * Find active employees by department.
     */
    @Query("SELECT e FROM Employee e WHERE e.department = :dept AND e.isActive = true")
    List<Employee> findActiveByDepartment(@Param("dept") String department);
}
