package com.fullstack.ticketflow.tickettype;

import com.fullstack.ticketflow.tickettype.dto.TicketTypeResponse;

public class TicketTypeMapper {

    private TicketTypeMapper() {}

    public static TicketTypeResponse toResponse(TicketType t) {
        return new TicketTypeResponse(
                t.getId(),
                t.getEvent().getId(),
                t.getName(),
                t.getPrice(),
                t.getTotalQty(),
                t.getSoldQty(),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}