package com.fullstack.ticketflow.role;

import com.fullstack.ticketflow.role.dto.RoleRequest;
import com.fullstack.ticketflow.role.dto.RoleResponse;
import org.springframework.stereotype.Component;

// Mapper manual (sin MapStruct) para no depender del procesamiento de
// anotaciones en el IDE: garantiza que el bean exista siempre.
@Component
public class RoleMapper {

    public Role toEntity(RoleRequest request) {
        return Role.builder()
                .name(request.name())
                .build();
    }

    public RoleResponse toResponse(Role role) {
        return new RoleResponse(role.getId(), role.getName(), role.getCreatedAt());
    }
}
