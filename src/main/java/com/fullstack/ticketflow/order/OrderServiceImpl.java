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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.fullstack.ticketflow.tickettype.dto.StockUpdateMessage;
import com.fullstack.ticketflow.report.dto.MetricsUpdateMessage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate; // Inyectado correctamente

    // Difunde el stock SOLO cuando la transacción confirma (afterCommit). Así
    // nunca se envía un valor que un rollback dejaría inválido. Envuelto en
    // try/catch para que un fallo de mensajería no afecte a la orden ya creada.
    private void broadcastStock(Integer ticketTypeId, int availableStock) {
        Runnable task = () -> {
            try {
                log.info("WS stock-update → ticketTypeId={} stock={}", ticketTypeId, availableStock);
                messagingTemplate.convertAndSend(
                        "/topic/stock-updates",
                        new StockUpdateMessage(ticketTypeId, availableStock));
            } catch (Exception ex) {
                log.error("WS: fallo al difundir stock (la operación ya está confirmada): {}", ex.getMessage());
            }
        };
        runAfterCommit(task);
    }

    // Avisa a los dashboards del administrador que las ventas cambiaron, para
    // que refresquen sus métricas en tiempo real (también tras el commit).
    private void broadcastMetricsChanged() {
        Runnable task = () -> {
            try {
                log.info("WS metrics-update → notificando a dashboards admin");
                messagingTemplate.convertAndSend(
                        "/topic/metrics-updates",
                        new MetricsUpdateMessage(System.currentTimeMillis()));
            } catch (Exception ex) {
                log.error("WS: fallo al difundir métricas: {}", ex.getMessage());
            }
        };
        runAfterCommit(task);
    }

    private void runAfterCommit(Runnable task) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    task.run();
                }
            });
        } else {
            task.run();
        }
    }

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
                ticket.setTicketType(ticketType);
                ticket.setStatus(TicketStatus.VALID);
                ticket.setQrCode(UUID.randomUUID().toString());

                orderItem.getTickets().add(ticket);
            }

            order.getOrderItems().add(orderItem);
        }

        order.setTotalAmount(orderTotal);

        Order savedOrder = orderRepository.save(order);

        // --- TRANSMISIÓN POR WEBSOCKET (COMPRA EXITOSA) ---
        // Se difunde el nuevo stock de cada categoría y se avisa a los
        // dashboards admin. Ambos se emiten en afterCommit (ver helpers).
        savedOrder.getOrderItems().forEach(oi -> {
            TicketType tt = oi.getTicketType();
            int currentStock = tt.getTotalQty() - tt.getSoldQty();
            broadcastStock(tt.getId(), currentStock);
        });
        broadcastMetricsChanged();
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

        // --- TRANSMISIÓN POR WEBSOCKET (CANCELACIÓN - STOCK LIBERADO) ---
        int currentStock = savedTicketType.getTotalQty() - savedTicketType.getSoldQty();
        broadcastStock(savedTicketType.getId(), currentStock);
        broadcastMetricsChanged();
        // ----------------------------------------------------------------------------------
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(String userEmail, String orderId) {
        User requester = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        if (!isAdmin(requester) && !order.getUser().getEmail().equals(userEmail)) {
            throw new BusinessRuleException("No tienes autorización para ver esta orden.");
        }

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

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders(String requesterEmail) {
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!isAdmin(requester)) {
            throw new BusinessRuleException("Solo el administrador puede ver todas las órdenes.");
        }

        return orderRepository.findAllByOrderByPaidAtDesc().stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrganizerSales(String organizerEmail) {
        User organizer = userRepository.findByEmail(organizerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Organizador no encontrado"));

        boolean isOrganizer = organizer.getRole() != null && "ORGANIZER".equals(organizer.getRole().getName());
        if (!isOrganizer) {
            throw new BusinessRuleException("Solo el organizador puede ver sus ventas.");
        }

        return orderRepository.findSalesByOrganizerEmail(organizerEmail).stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<Ticket> tickets = new ArrayList<>();
        order.getOrderItems().forEach(oi -> tickets.addAll(oi.getTickets()));
        return mapToOrderResponse(order, tickets);
    }

    private OrderResponse mapToOrderResponse(Order order, List<Ticket> tickets) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .userEmail(order.getUser().getEmail())
                .userFullName(order.getUser().getFullName())
                .total(order.getTotalAmount())
                .paymentStatus(order.getPaymentStatus().name())
                .paymentMethod(order.getPaymentMethod())
                .orderDate(order.getPaidAt() != null ? order.getPaidAt() : order.getCreatedAt())
                .generatedTickets(tickets.stream().map(t -> TicketResponse.builder()
                        .id(t.getId())
                        .qrCode(t.getQrCode())
                        .status(t.getStatus().name())
                        .eventId(t.getOrderItem().getTicketType().getEvent().getId())
                        .eventTitle(t.getOrderItem().getTicketType().getEvent().getTitle())
                        .ticketTypeName(t.getOrderItem().getTicketType().getName())
                        .createdAt(t.getCreatedAt())
                        .build()).collect(Collectors.toList()))
                .build();
    }

    private boolean isAdmin(User user) {
        return user.getRole() != null && "ADMIN".equals(user.getRole().getName());
    }
}
