package com.fullstack.ticketflow.ticket.dto;

import java.time.LocalDateTime;

public record TicketResponse(
        String id,
        String orderItemId,
        Integer ticketTypeId,
        String qrCode,
        String holderName,
        boolean isVoid,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
