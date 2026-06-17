package com.fullstack.ticketflow.event.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record EventRequest(
        @NotBlank @Size(max = 150) String title,
        @NotBlank String description,
        @NotNull @Future LocalDateTime dateTime,
        String imageUrl,
        @NotNull Integer venueId
) {}