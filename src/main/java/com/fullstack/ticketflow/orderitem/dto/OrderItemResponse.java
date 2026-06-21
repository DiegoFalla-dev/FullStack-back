package com.fullstack.ticketflow.orderitem.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderItemResponse(
        String id,
        String orderId,
        Integer ticketTypeId,
        Integer quantity,
        BigDecimal unitPrice,
        LocalDateTime createdAt
) {
}
