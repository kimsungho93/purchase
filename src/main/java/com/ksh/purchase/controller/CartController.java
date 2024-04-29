package com.ksh.purchase.controller;

import com.ksh.purchase.controller.reqeust.CartAddItemRequest;
import com.ksh.purchase.controller.reqeust.CartQuantityUpdateRequest;
import com.ksh.purchase.controller.response.CartAddItemResponse;
import com.ksh.purchase.controller.response.CartProductResponse;
import com.ksh.purchase.entity.CartProduct;
import com.ksh.purchase.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CartController {
    private final CartService cartService;

    // 장바구니 추가
    @PostMapping("/api/v1/carts")
    public ResponseEntity<CartAddItemResponse> addItemToCart(@AuthenticationPrincipal User user, @Valid @RequestBody CartAddItemRequest request) {
        String userId = user.getUsername();
        return ResponseEntity.ok().body(cartService.addItemtoCart(userId, request));
    }

    // 장바구니 조회
    @GetMapping("/api/v1/carts")
    public ResponseEntity<Set<CartProductResponse>> getCart(@AuthenticationPrincipal User user) {
        String userId = user.getUsername();
        return ResponseEntity.ok().body(cartService.getCart(userId));
    }

    // 수량 변경
    @PostMapping(value = "/api/v1/carts/change-quantity")
    public ResponseEntity<CartProductResponse> updateQuantity(@AuthenticationPrincipal User user, @Valid @RequestBody CartQuantityUpdateRequest request) {
        String userId = user.getUsername();
        return ResponseEntity.ok().body(cartService.updateQuantity(userId, request));
    }


}
