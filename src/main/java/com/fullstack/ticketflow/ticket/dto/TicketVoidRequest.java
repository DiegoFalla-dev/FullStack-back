package com.fullstack.ticketflow.ticket.dto;

import jakarta.validation.constraints.NotNull;

public record TicketVoidRequest(
        @NotNull Boolean isVoid
) {
}
