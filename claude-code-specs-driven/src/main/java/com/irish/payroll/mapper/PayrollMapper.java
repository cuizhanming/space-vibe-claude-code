package com.irish.payroll.mapper;

import com.irish.payroll.dto.response.PayrollResponse;
import com.irish.payroll.entity.Payroll;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for Payroll entity.
 */
@Mapper(componentModel = "spring", uses = {PayslipMapper.class})
public interface PayrollMapper {

    /**
     * Map payroll entity to response DTO.
     */
    @Mapping(target = "payslips", source = "payslips")
    PayrollResponse toResponse(Payroll payroll);
}
