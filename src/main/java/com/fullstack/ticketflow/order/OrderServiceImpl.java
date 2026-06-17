package com.fullstack.ticketflow.order;

import com.fullstack.ticketflow.order.dto.OrderItemRequest;
import com.fullstack.ticketflow.order.dto.OrderRequest;
import com.fullstack.ticketflow.order.dto.OrderResponse;
import com.fullstack.ticketflow.order.enums.PaymentStatus;
import com.fullstack.ticketflow.orderitem.OrderItem;
import com.fullstack.ticketflow.shared.exception.BusinessRuleException;
import com.fullstack.ticketflow.shared.exception.InsufficientStockException;
import com.fullstack.ticketflow.shared.exception.ResourceNotFoundException;
import com.fullstack.ticketflow.ticket.Ticket;
import com.fullstack.ticketflow.ticket.TicketRepository;
import com.fullstack.ticketflow.ticket.dto.TicketResponse;
import com.fullstack.ticketflow.ticket.enums.TicketStatus;
import com.fullstack.ticketflow.tickettype.TicketType;
import com.fullstack.ticketflow.tickettype.TicketTypeRepository;
import com.fullstack.ticketflow.user.User;
import com.fullstack.ticketflow.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.fullstack.ticketflow.tickettype.dto.StockUpdateMessage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate; // Inyectado correctamente

    @Override
    @Transactional
    public OrderResponse createOrder(String userEmail, OrderRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + userEmail));

        Order order = new Order();
        order.setId(UUID.randomUUID().toString());
        order.setUser(user);
        order.setPaidAt(LocalDateTime.now());
        order.setPaymentStatus(PaymentStatus.PAID);
        order.setPaymentMethod(request.paymentMethod());

        BigDecimal orderTotal = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.items()) {
            TicketType ticketType = ticketTypeRepository.findByIdWithLock(itemReq.ticketTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada: " + itemReq.ticketTypeId()));

            int availableStock = ticketType.getTotalQty() - ticketType.getSoldQty();
            if (itemReq.quantity() > availableStock) {
                throw new InsufficientStockException("Stock insuficiente para: " + ticketType.getName());
            }

            ticketType.setSoldQty(ticketType.getSoldQty() + itemReq.quantity());
            ticketTypeRepository.save(ticketType);

            BigDecimal subtotalItem = ticketType.getPrice().multiply(BigDecimal.valueOf(itemReq.quantity()));
            orderTotal = orderTotal.add(subtotalItem);

            OrderItem orderItem = new OrderItem();
            orderItem.setId(UUID.randomUUID().toString());
            orderItem.setOrder(order);
            orderItem.setTicketType(ticketType);
            orderItem.setQuantity(itemReq.quantity());
            orderItem.setUnitPrice(ticketType.getPrice());

            for (int i = 0; i < itemReq.quantity(); i++) {
                Ticket ticket = new Ticket();
                ticket.setId(UUID.randomUUID().toString());
                ticket.setOrderItem(orderItem);
                ticket.setStatus(TicketStatus.VALID);
                ticket.setQrCode(UUID.randomUUID().toString());

                orderItem.getTickets().add(ticket);
            }

            order.getOrderItems().add(orderItem);
        }

        order.setTotalAmount(orderTotal);

        Order savedOrder = orderRepository.save(order);

        // --- PIEZA FALTANTE 1: TRANSMISIÓN POR WEBSOCKET (COMPRA EXITOSA) ---
        savedOrder.getOrderItems().forEach(oi -> {
            TicketType tt = oi.getTicketType();
            int currentStock = tt.getTotalQty() - tt.getSoldQty();

            // Enviamos el ID y el nuevo stock calculado en tiempo real
            messagingTemplate.convertAndSend(
                    "/topic/stock-updates",
                    new StockUpdateMessage(tt.getId(), currentStock)
            );
        });
        // --------------------------------------------------------------------

        List<Ticket> generatedTickets = new ArrayList<>();
        savedOrder.getOrderItems().forEach(oi -> generatedTickets.addAll(oi.getTickets()));

        return mapToOrderResponse(savedOrder, generatedTickets);
    }

    @Override
    @Transactional
    public void cancelTicket(String userEmail, String ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Boleto no encontrado"));

        if (!ticket.getOrderItem().getOrder().getUser().getEmail().equals(userEmail)) {
            throw new BusinessRuleException("No tienes autorización sobre esta entrada.");
        }

        if (ticket.getStatus() != TicketStatus.VALID) {
            throw new BusinessRuleException("El estado actual del boleto no permite su cancelación.");
        }

        LocalDateTime eventDateTime = ticket.getOrderItem().getTicketType().getEvent().getDateTime();
        if (LocalDateTime.now().isAfter(eventDateTime.minusHours(72))) {
            throw new BusinessRuleException("Solo se permiten cancelaciones con un mínimo de 72 horas de anticipación al evento.");
        }

        ticket.setStatus(TicketStatus.CANCELLED);
        ticketRepository.save(ticket);

        TicketType ticketType = ticketTypeRepository.findByIdWithLock(ticket.getOrderItem().getTicketType().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Error al restaurar inventario"));

        ticketType.setSoldQty(ticketType.getSoldQty() - 1);
        TicketType savedTicketType = ticketTypeRepository.save(ticketType);

        // --- PIEZA FALTANTE 2: TRANSMISIÓN POR WEBSOCKET (CANCELACIÓN - STOCK LIBERADO) ---
        int currentStock = savedTicketType.getTotalQty() - savedTicketType.getSoldQty();
        messagingTemplate.convertAndSend(
                "/topic/stock-updates",
                new StockUpdateMessage(savedTicketType.getId(), currentStock)
        );
        // ----------------------------------------------------------------------------------
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        List<Ticket> tickets = new ArrayList<>();
        order.getOrderItems().forEach(oi -> tickets.addAll(oi.getTickets()));

        return mapToOrderResponse(order, tickets);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrderHistory(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return orderRepository.findByUserIdOrderByPaidAtDesc(user.getId()).stream()
                .map(order -> {
                    List<Ticket> tickets = new ArrayList<>();
                    order.getOrderItems().forEach(oi -> tickets.addAll(oi.getTickets()));
                    return mapToOrderResponse(order, tickets);
                })
                .collect(Collectors.toList());
    }

    private OrderResponse mapToOrderResponse(Order order, List<Ticket> tickets) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .total(order.getTotalAmount())
                .paymentStatus(order.getPaymentStatus().name())
                .paymentMethod(order.getPaymentMethod())
                .orderDate(order.getPaidAt() != null ? order.getPaidAt() : order.getCreatedAt())
                .generatedTickets(tickets.stream().map(t -> TicketResponse.builder()
                        .id(t.getId())
                        .qrCode(t.getQrCode())
                        .status(t.getStatus().name())
                        .ticketTypeName(t.getOrderItem().getTicketType().getName())
                        .createdAt(t.getCreatedAt())
                        .build()).collect(Collectors.toList()))
                .build();
    }
}