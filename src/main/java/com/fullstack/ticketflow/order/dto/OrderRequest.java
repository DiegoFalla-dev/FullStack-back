package com.fullstack.ticketflow.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record OrderRequest(
        @NotNull(message = "El método de pago es obligatorio")
        String paymentMethod,

        @NotEmpty(message = "La orden debe contener al menos un ítem")
        List<@Valid OrderItemRequest> items
) {}