package com.ksh.purchase.controller;

import com.ksh.purchase.controller.reqeust.CreateOrderRequest;
import com.ksh.purchase.controller.response.OrderResponse;
import com.ksh.purchase.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

}
