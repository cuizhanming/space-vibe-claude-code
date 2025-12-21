package com.irish.payroll.mapper;

import com.irish.payroll.dto.response.PayslipResponse;
import com.irish.payroll.entity.Payslip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for Payslip entity.
 */
@Mapper(componentModel = "spring")
public interface PayslipMapper {

    /**
     * Map payslip entity to response DTO.
     */
    @Mapping(target = "payrollId", source = "payroll.id")
    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "employeeName", expression = "java(payslip.getEmployee().getFullName())")
    @Mapping(target = "employeePpsNumber", source = "employee.ppsNumber")
    PayslipResponse toResponse(Payslip payslip);
}
