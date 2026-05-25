package com.fullstack.ticketflow.role;

import com.fullstack.ticketflow.role.dto.RoleRequest;
import com.fullstack.ticketflow.role.dto.RoleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleService {
    Page<RoleResponse> list(Pageable pageable);
    RoleResponse getById(Short id);
    RoleResponse create(RoleRequest request);
    RoleResponse update(Short id, RoleRequest request);
    void delete(Short id);
}
