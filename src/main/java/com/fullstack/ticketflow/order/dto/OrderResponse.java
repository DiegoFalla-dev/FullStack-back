package com.fullstack.ticketflow.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        String id,
        String userId,
        String status,
        BigDecimal totalAmount,
        LocalDateTime paidAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
