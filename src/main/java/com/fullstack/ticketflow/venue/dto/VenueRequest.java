package com.fullstack.ticketflow.venue.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VenueRequest(
        @NotBlank @Size(max = 150) String name,
        @NotBlank String address,
        @NotBlank @Size(max = 100) String city,
        @NotNull @Min(1) Integer capacity
) {}