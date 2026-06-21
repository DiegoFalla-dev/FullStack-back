package com.fullstack.ticketflow.event.dto;

import com.fullstack.ticketflow.venue.dto.VenueResponse;
import java.time.LocalDateTime;
import java.math.BigDecimal;

public record EventResponse(
        Integer id,
        String title,
        String description,
        LocalDateTime dateTime,
        String imageUrl,
        String status,
        VenueResponse venue,
        String organizerName,
        BigDecimal minPrice // Auxiliar para mostrar el precio "Desde S/ X" en las tarjetas del Frontend
) {}