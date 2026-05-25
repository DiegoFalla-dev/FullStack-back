package com.fullstack.ticketflow.role;

import com.fullstack.ticketflow.role.dto.RoleRequest;
import com.fullstack.ticketflow.role.dto.RoleResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    Role toEntity(RoleRequest request);
    RoleResponse toResponse(Role role);
}
