package com.fullstack.ticketflow.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequest(
        @NotNull(message = "El ID del tipo de ticket es obligatorio")
        Integer ticketTypeId,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad mínima a comprar es 1")
        Integer quantity
) {}