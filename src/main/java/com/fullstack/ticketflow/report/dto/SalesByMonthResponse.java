package com.fullstack.ticketflow.report.dto;

import java.math.BigDecimal;

public record SalesByMonthResponse(
        String month,
        BigDecimal totalSales
) {
}
