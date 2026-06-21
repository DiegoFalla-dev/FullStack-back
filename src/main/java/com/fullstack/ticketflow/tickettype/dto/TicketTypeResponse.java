package com.fullstack.ticketflow.tickettype.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TicketTypeResponse(
        Integer id,
        Integer eventId,
        String name,
        BigDecimal price,
        Integer totalQty,
        Integer soldQty,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
