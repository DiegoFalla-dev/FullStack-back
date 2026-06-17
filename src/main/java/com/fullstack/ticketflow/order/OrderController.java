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
}