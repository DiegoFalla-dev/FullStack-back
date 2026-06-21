package com.fullstack.ticketflow.report.dto;

public record TicketsByCategoryMonthResponse(
        String month,
        String category,
        Long ticketsSold
) {
}
