package com.fullstack.ticketflow.category.dto;

import java.time.LocalDateTime;

public record CategoryResponse(
        Short id,
        String name,
        String description,
        LocalDateTime createdAt
) {
}
