package com.fullstack.ticketflow.report;

import com.fullstack.ticketflow.report.dto.ClientsByMonthResponse;
import com.fullstack.ticketflow.report.dto.SalesByMonthResponse;
import com.fullstack.ticketflow.report.dto.TicketsByCategoryMonthResponse;

import java.util.List;

public interface ReportService {
    List<ClientsByMonthResponse> getClientsByMonth();
    List<SalesByMonthResponse> getSalesByMonth();
    List<TicketsByCategoryMonthResponse> getTicketsByCategoryByMonth();
}
