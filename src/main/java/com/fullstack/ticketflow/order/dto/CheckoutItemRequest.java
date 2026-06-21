package com.fullstack.ticketflow.order.dto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CheckoutItemRequest(
        @NotNull Integer ticketTypeId,
        @NotNull @Positive Integer quantity
) {}