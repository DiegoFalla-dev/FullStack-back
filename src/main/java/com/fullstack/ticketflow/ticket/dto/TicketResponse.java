package com.fullstack.ticketflow.ticket.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class TicketResponse {
    private String id; // UUID
    private String qrCode;
    private String status;
    private Integer eventId;
    private String eventTitle;
    private Short eventCategoryId;
    private String eventCategoryName;
    private String ticketTypeName;
    private LocalDateTime createdAt;
}
