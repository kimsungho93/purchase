package com.ksh.purchase.controller;

import com.ksh.purchase.controller.reqeust.CartAddItemRequest;
import com.ksh.purchase.controller.reqeust.CartItemDeleteRequest;
import com.ksh.purchase.controller.reqeust.CartProductIdRequest;
import com.ksh.purchase.controller.reqeust.CartQuantityUpdateRequest;
import com.ksh.purchase.controller.response.CartAddItemResponse;
import com.ksh.purchase.controller.response.CartProductResponse;
import com.ksh.purchase.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    // 장바구니에서 상품 선택 바꾸기
    @PostMapping("/api/v1/carts/check")
    public ResponseEntity<?> checkItemFromCart(@AuthenticationPrincipal User user, @RequestBody List<CartProductIdRequest> cartProductIds) {
        String userId = user.getUsername();
        cartService.checkItemFromCart(userId, cartProductIds);
        return ResponseEntity.ok().build();
    }

    // 장바구니에서 상품 삭제(하나)
    @DeleteMapping("/api/v1/carts/{cartProductId}")
    public ResponseEntity<Void> deleteItemFromCart(@AuthenticationPrincipal User user, @PathVariable Long cartProductId) {
        String userId = user.getUsername();
        cartService.deleteItemFromCart(userId, cartProductId);
        return ResponseEntity.ok().build();
    }

    // 장바구니에서 선택된 상품만 삭제(여러개)
    @DeleteMapping("/api/v1/carts/checked")
    public ResponseEntity<Void> deleteCheckedItemsFromCart(@AuthenticationPrincipal User user, @RequestBody List<CartItemDeleteRequest> cartProductIds) {
        String userId = user.getUsername();
        cartService.deleteCheckedItemsFromCart(userId, cartProductIds);
        return ResponseEntity.ok().build();
    }

    // 장바구니에서 상품 전체 삭제
    @DeleteMapping("/api/v1/carts")
    public ResponseEntity<Void> deleteAllItemsFromCart(@AuthenticationPrincipal User user) {
        String userId = user.getUsername();
        cartService.deleteAllItemsFromCart(userId);
        return ResponseEntity.ok().build();
    }


    // 수량 변경
    @PostMapping(value = "/api/v1/carts/change-quantity")
    public ResponseEntity<CartProductResponse> updateQuantity(@AuthenticationPrincipal User user, @Valid @RequestBody CartQuantityUpdateRequest request) {
        String userId = user.getUsername();
        return ResponseEntity.ok().body(cartService.updateQuantity(userId, request));
    }


}
