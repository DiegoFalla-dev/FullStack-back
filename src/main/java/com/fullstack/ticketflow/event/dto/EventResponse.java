package com.fullstack.ticketflow.event.dto;

import java.time.LocalDateTime;

public record EventResponse(
        Integer id,
        Integer venueId,
        String venueName,
        String name,
        String description,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
