package com.fullstack.ticketflow.role.dto;

import java.time.LocalDateTime;

public record RoleResponse(Short id, String name, LocalDateTime createdAt) {
}
