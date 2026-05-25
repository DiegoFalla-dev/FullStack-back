package com.fullstack.ticketflow.orderitem.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record OrderItemRequest(
        @NotNull String orderId,
        @NotNull Integer ticketTypeId,
        @NotNull @Positive Integer quantity,
        @NotNull BigDecimal unitPrice
) {
}
