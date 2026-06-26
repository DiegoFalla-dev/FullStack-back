package com.fullstack.ticketflow.report;

import com.fullstack.ticketflow.report.dto.ClientsByMonthResponse;
import com.fullstack.ticketflow.report.dto.SalesByMonthResponse;
import com.fullstack.ticketflow.report.dto.TicketsByCategoryMonthResponse;
import com.fullstack.ticketflow.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/clients-by-month")
    public ResponseEntity<ApiResponse<List<ClientsByMonthResponse>>> getClientsByMonth() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getClientsByMonth()));
    }

    @GetMapping("/sales-by-month")
    public ResponseEntity<ApiResponse<List<SalesByMonthResponse>>> getSalesByMonth() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getSalesByMonth()));
    }

    @GetMapping("/tickets-by-category-by-month")
    public ResponseEntity<ApiResponse<List<TicketsByCategoryMonthResponse>>> getTicketsByCategoryByMonth() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getTicketsByCategoryByMonth()));
    }

    // Entradas vendidas agrupadas por la CATEGORÍA del evento (Concierto,
    // Teatro, etc.), distinto del reporte anterior que agrupa por tipo de ticket.
    @GetMapping("/tickets-by-event-category-by-month")
    public ResponseEntity<ApiResponse<List<TicketsByCategoryMonthResponse>>> getTicketsByEventCategoryByMonth() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getTicketsByEventCategoryByMonth()));
    }
}
