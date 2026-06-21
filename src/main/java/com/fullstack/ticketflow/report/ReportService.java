package com.fullstack.ticketflow.report;

import com.fullstack.ticketflow.report.dto.SalesByMonthResponse;
import com.fullstack.ticketflow.report.dto.TicketsByCategoryMonthResponse;

import java.util.List;

public interface ReportService {
    List<SalesByMonthResponse> getSalesByMonth();
    List<TicketsByCategoryMonthResponse> getTicketsByCategoryByMonth();
}
