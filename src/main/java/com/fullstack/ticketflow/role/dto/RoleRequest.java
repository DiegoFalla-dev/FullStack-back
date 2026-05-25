package com.fullstack.ticketflow.role.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RoleRequest(
        @NotBlank @Size(max = 50) String name
) {
}
