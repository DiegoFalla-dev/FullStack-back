package com.fullstack.ticketflow.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record EventRequest(
        @NotNull Integer venueId,
        @NotBlank @Size(max = 200) String name,
        String description,
        @NotNull LocalDateTime startsAt,
        @NotNull LocalDateTime endsAt
) {
}
