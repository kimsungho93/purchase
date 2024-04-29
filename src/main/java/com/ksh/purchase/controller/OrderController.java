package com.ksh.purchase.controller;

import com.ksh.purchase.controller.reqeust.CreateOrderRequest;
import com.ksh.purchase.controller.response.OrderCancelResponse;
import com.ksh.purchase.controller.response.OrderResponse;
import com.ksh.purchase.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    // 주문(상품 재고 차감)
    @PostMapping("/api/v1/orders")
    public ResponseEntity<OrderResponse> createOrder(@AuthenticationPrincipal User user, @Valid @RequestBody List<CreateOrderRequest> request) {
        long userId = Long.parseLong(user.getUsername());
        return ResponseEntity.ok(orderService.createOrder(userId, request));
    }

    // 주문 취소(상품 재고 복구)
    @PostMapping("/api/v1/orders/{orderId}/cancel")
    public ResponseEntity<OrderCancelResponse> cancelOrder(@AuthenticationPrincipal User user, @PathVariable long orderId) {
        long userId = Long.parseLong(user.getUsername());
        return ResponseEntity.ok(orderService.cancelOrder(userId, orderId));
    }

    // 주문 목록 조회
    @GetMapping("/api/v1/orders")
    public ResponseEntity<List<OrderResponse>> getOrders(@AuthenticationPrincipal User user) {
        long userId = Long.parseLong(user.getUsername());
        return ResponseEntity.ok().body(orderService.getOrders(userId));
    }

}
