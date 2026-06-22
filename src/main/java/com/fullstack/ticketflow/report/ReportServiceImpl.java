package com.fullstack.ticketflow.report;

import com.fullstack.ticketflow.order.Order;
import com.fullstack.ticketflow.order.OrderRepository;
import com.fullstack.ticketflow.order.enums.PaymentStatus;
import com.fullstack.ticketflow.orderitem.OrderItem;
import com.fullstack.ticketflow.report.dto.ClientsByMonthResponse;
import com.fullstack.ticketflow.report.dto.SalesByMonthResponse;
import com.fullstack.ticketflow.report.dto.TicketsByCategoryMonthResponse;
import com.fullstack.ticketflow.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReportServiceImpl implements ReportService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ClientsByMonthResponse> getClientsByMonth() {
        Map<YearMonth, Long> totals = new TreeMap<>();

        userRepository.findAll().stream()
                .filter(user -> user.getRole() != null)
                .filter(user -> "CLIENT".equals(user.getRole().getName()))
                .forEach(user -> totals.merge(
                        YearMonth.from(user.getCreatedAt()),
                        1L,
                        Long::sum
                ));

        return totals.entrySet().stream()
                .map(entry -> new ClientsByMonthResponse(entry.getKey().toString(), entry.getValue()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalesByMonthResponse> getSalesByMonth() {
        Map<YearMonth, BigDecimal> totals = new TreeMap<>();

        orderRepository.findAll().stream()
                .filter(order -> order.getPaymentStatus() == PaymentStatus.PAID)
                .forEach(order -> totals.merge(
                        getOrderMonth(order),
                        order.getTotalAmount(),
                        BigDecimal::add
                ));

        return totals.entrySet().stream()
                .map(entry -> new SalesByMonthResponse(entry.getKey().toString(), entry.getValue()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketsByCategoryMonthResponse> getTicketsByCategoryByMonth() {
        Map<CategoryMonthKey, Long> totals = new LinkedHashMap<>();

        orderRepository.findAll().stream()
                .filter(order -> order.getPaymentStatus() == PaymentStatus.PAID)
                .sorted(Comparator.comparing(this::getOrderMonth))
                .forEach(order -> {
                    YearMonth month = getOrderMonth(order);
                    for (OrderItem item : order.getOrderItems()) {
                        CategoryMonthKey key = new CategoryMonthKey(month, item.getTicketType().getName());
                        totals.merge(key, item.getQuantity().longValue(), Long::sum);
                    }
                });

        return totals.entrySet().stream()
                .map(entry -> new TicketsByCategoryMonthResponse(
                        entry.getKey().month().toString(),
                        entry.getKey().category(),
                        entry.getValue()
                ))
                .collect(Collectors.toList());
    }

    private YearMonth getOrderMonth(Order order) {
        return YearMonth.from(order.getPaidAt() != null ? order.getPaidAt() : order.getCreatedAt());
    }

    private record CategoryMonthKey(YearMonth month, String category) {
    }
}
