package com.irish.payroll.repository;

import com.irish.payroll.entity.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Payslip entity.
 */
@Repository
public interface PayslipRepository extends JpaRepository<Payslip, UUID> {

    /**
     * Find payslips by employee ID ordered by created date descending.
     */
    List<Payslip> findByEmployeeIdOrderByCreatedDateDesc(UUID employeeId);

    /**
     * Find payslips by payroll ID.
     */
    List<Payslip> findByPayrollId(UUID payrollId);

    /**
     * Calculate year-to-date gross pay for an employee.
     */
    @Query("SELECT COALESCE(SUM(p.grossPay), 0) FROM Payslip p " +
           "WHERE p.employee.id = :empId AND YEAR(p.createdDate) = :year")
    BigDecimal calculateYtdGross(@Param("empId") UUID employeeId, @Param("year") int year);

    /**
     * Calculate year-to-date PAYE for an employee.
     */
    @Query("SELECT COALESCE(SUM(p.payeDeduction), 0) FROM Payslip p " +
           "WHERE p.employee.id = :empId AND YEAR(p.createdDate) = :year")
    BigDecimal calculateYtdPaye(@Param("empId") UUID employeeId, @Param("year") int year);

    /**
     * Calculate year-to-date PRSI for an employee.
     */
    @Query("SELECT COALESCE(SUM(p.prsiDeduction), 0) FROM Payslip p " +
           "WHERE p.employee.id = :empId AND YEAR(p.createdDate) = :year")
    BigDecimal calculateYtdPrsi(@Param("empId") UUID employeeId, @Param("year") int year);

    /**
     * Calculate year-to-date USC for an employee.
     */
    @Query("SELECT COALESCE(SUM(p.uscDeduction), 0) FROM Payslip p " +
           "WHERE p.employee.id = :empId AND YEAR(p.createdDate) = :year")
    BigDecimal calculateYtdUsc(@Param("empId") UUID employeeId, @Param("year") int year);
}
