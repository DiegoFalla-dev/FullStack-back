package com.fullstack.ticketflow.user.dto;

import java.time.LocalDateTime;

public record UserResponse(
        String id,
        String email,
        String fullName,
        Short roleId,
        String roleName,
        boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
