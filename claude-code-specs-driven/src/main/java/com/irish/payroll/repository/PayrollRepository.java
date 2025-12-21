package com.irish.payroll.repository;

import com.irish.payroll.entity.Payroll;
import com.irish.payroll.entity.PayrollStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Payroll entity.
 */
@Repository
public interface PayrollRepository extends JpaRepository<Payroll, UUID> {

    /**
     * Find payrolls between dates.
     */
    List<Payroll> findByPayPeriodStartBetween(LocalDate start, LocalDate end);

    /**
     * Find payroll by exact period.
     */
    Optional<Payroll> findByPayPeriodStartAndPayPeriodEnd(LocalDate start, LocalDate end);

    /**
     * Find payrolls by status.
     */
    List<Payroll> findByStatus(PayrollStatus status);

    /**
     * Find all payrolls ordered by date descending.
     */
    List<Payroll> findAllByOrderByPayPeriodEndDesc();
}
