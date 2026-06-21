package com.fullstack.ticketflow.ticket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TicketRequest(
        @NotBlank @Size(max = 36) String orderItemId,
        @NotNull Integer ticketTypeId,
        @NotBlank @Size(max = 120) String holderName
) {
}
