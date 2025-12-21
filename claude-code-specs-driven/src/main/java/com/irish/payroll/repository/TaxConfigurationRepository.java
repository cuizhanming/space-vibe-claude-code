package com.irish.payroll.repository;

import com.irish.payroll.entity.TaxConfiguration;
import com.irish.payroll.entity.TaxType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for TaxConfiguration entity.
 */
@Repository
public interface TaxConfigurationRepository extends JpaRepository<TaxConfiguration, UUID> {

    /**
     * Find active tax configurations by year and type.
     */
    List<TaxConfiguration> findByTaxYearAndTaxTypeAndIsActiveTrue(Integer year, TaxType type);

    /**
     * Find active tax bands ordered by income lower bound.
     */
    @Query("SELECT tc FROM TaxConfiguration tc " +
           "WHERE tc.taxYear = :year AND tc.taxType = :type AND tc.isActive = true " +
           "ORDER BY tc.incomeLower")
    List<TaxConfiguration> findActiveTaxBands(@Param("year") Integer year, @Param("type") TaxType type);
}
