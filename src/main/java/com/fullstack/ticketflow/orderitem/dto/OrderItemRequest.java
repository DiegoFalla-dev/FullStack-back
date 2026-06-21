package com.fullstack.ticketflow.orderitem.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderItemRequest {
    @NotNull(message = "El ID del tipo de ticket es obligatorio")
    private Integer ticketTypeId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima a comprar es 1")
    private Integer quantity;
}
