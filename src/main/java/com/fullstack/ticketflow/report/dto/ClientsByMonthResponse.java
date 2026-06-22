package com.fullstack.ticketflow.report.dto;

public record ClientsByMonthResponse(
        String month,
        Long totalClients
) {
}
