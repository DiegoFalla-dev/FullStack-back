package com.fullstack.ticketflow.order;

import com.fullstack.ticketflow.order.dto.OrderRequest;
import com.fullstack.ticketflow.order.dto.OrderResponse;
import java.util.List;

public interface OrderService {
    OrderResponse createOrder(String userEmail, OrderRequest request);
    void cancelTicket(String userEmail, String ticketId);
    OrderResponse getOrderById(String userEmail, String orderId);
    List<OrderResponse> getUserOrderHistory(String userEmail);
    List<OrderResponse> getAllOrders(String requesterEmail);
    List<OrderResponse> getOrganizerSales(String organizerEmail);
}
