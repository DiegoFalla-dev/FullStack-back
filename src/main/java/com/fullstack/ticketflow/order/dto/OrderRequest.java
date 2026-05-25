package com.fullstack.ticketflow.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record OrderRequest(
        @NotBlank @Size(max = 36) String userId,
        @NotBlank @Size(max = 20) String status,
        @NotNull BigDecimal totalAmount
) {
}
