package com.fullstack.ticketflow.order.dto;

import com.fullstack.ticketflow.ticket.dto.TicketResponse;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private String id; // UUID
    private String userId; // UUID
    private BigDecimal total;
    private String paymentStatus;
    private String paymentMethod;
    private LocalDateTime orderDate;
    private List<TicketResponse> generatedTickets;
}