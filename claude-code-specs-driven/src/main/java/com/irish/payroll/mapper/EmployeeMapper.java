package com.irish.payroll.mapper;

import com.irish.payroll.dto.request.EmployeeCreateRequest;
import com.irish.payroll.dto.response.EmployeeResponse;
import com.irish.payroll.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for Employee entity.
 */
@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    /**
     * Map employee entity to response DTO.
     */
    EmployeeResponse toResponse(Employee employee);

    /**
     * Map create request DTO to employee entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "payslips", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    Employee toEntity(EmployeeCreateRequest request);
}
