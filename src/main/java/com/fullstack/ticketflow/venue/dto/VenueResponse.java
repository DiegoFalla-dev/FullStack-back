package com.fullstack.ticketflow.venue.dto;

import java.time.LocalDateTime;

public record VenueResponse(
        Integer id,
        String name,
        String address,
        Integer capacity,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
