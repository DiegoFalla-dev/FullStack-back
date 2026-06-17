package com.fullstack.ticketflow.order;

import com.fullstack.ticketflow.order.dto.OrderRequest;
import com.fullstack.ticketflow.order.dto.OrderResponse;
import com.fullstack.ticketflow.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody OrderRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(orderService.createOrder(authentication.getName(), request)));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyHistory(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getUserOrderHistory(authentication.getName())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderById(id)));
    }

    // NUEVO: faltaba exponer cancelTicket, que ya existía en
    // OrderServiceImpl con la regla de 72h implementada. Sin este
    // endpoint, el front no tenía ninguna forma de llamarlo.
    @PatchMapping("/tickets/{ticketId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelTicket(
            @PathVariable String ticketId,
            Authentication authentication) {
        orderService.cancelTicket(authentication.getName(), ticketId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}