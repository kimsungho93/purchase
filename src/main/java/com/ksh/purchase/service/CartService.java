package com.ksh.purchase.service;

import com.ksh.purchase.controller.reqeust.CartAddItemRequest;
import com.ksh.purchase.controller.reqeust.CartQuantityUpdateRequest;
import com.ksh.purchase.controller.response.CartAddItemResponse;
import com.ksh.purchase.controller.response.CartProductResponse;
import com.ksh.purchase.entity.CartProduct;
import com.ksh.purchase.entity.Product;
import com.ksh.purchase.entity.User;
import com.ksh.purchase.exception.CustomException;
import com.ksh.purchase.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final UserService userService;
    private final ProductService productService;

    // 상품을 장바구니에 추가
    public CartAddItemResponse addItemToCart(String userId, CartAddItemRequest request) {
        User user = userService.findById(Long.parseLong(userId));
        Product product = productService.findBtyId(request.productId());

        CartProduct cartProduct = findOrCreateCartProduct(user, product, request.quantity());
        userService.save(user);
        return createCartAddItemResponse(cartProduct);
    }

    // 장바구니 조회
    public Set<CartProductResponse> getCart(String userId) {
        User user = userService.findById(Long.parseLong(userId));
        return user.getCart().getCartProducts().stream()
                .filter(cartProduct -> !cartProduct.isDeleted())
                .map(CartProductResponse::of)
                .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(CartProductResponse::getCreatedAt).reversed())));
    }

    // 장바구니 상품 삭제 (단일)
    public void deleteItemFromCart(String userId, Long cartProductId) {
        User user = userService.findById(Long.parseLong(userId));
        CartProduct cartProduct = findCartProduct(user, cartProductId);
        markProductAsDeleted(cartProduct);
        userService.save(user);
    }

    // 장바구니 상품 삭제 (선택된 상품들)
    public void deleteSelectedItemsFromCart(String userId) {
        User user = userService.findById(Long.parseLong(userId));
        user.getCart().getCartProducts().stream()
                .filter(CartProduct::isChecked)
                .forEach(this::markProductAsDeleted);
        userService.save(user);
    }

    // 장바구니 전체 상품 삭제
    public void deleteAllItemsFromCart(String userId) {
        User user = userService.findById(Long.parseLong(userId));
        user.getCart().getCartProducts().forEach(this::markProductAsDeleted);
        userService.save(user);
    }

    // 장바구니 상품 수량 변경
    public CartProductResponse updateQuantity(String userId, CartQuantityUpdateRequest request) {
        User user = userService.findById(Long.parseLong(userId));
        CartProduct cartProduct = findCartProduct(user, request.cartProductId());
        cartProduct.updateQuantity(request.quantity());
        userService.save(user);
        return CartProductResponse.of(cartProduct);
    }

    // 장바구니 상품 선택 토글
    public void toggleCartItemSelection(String userId, Long cartProductId) {
        User user = userService.findById(Long.parseLong(userId));
        CartProduct cartProduct = findCartProduct(user, cartProductId);
        cartProduct.setChecked(!cartProduct.isChecked());
        userService.save(user);
    }

    // 상품을 찾거나 새로 생성
    private CartProduct findOrCreateCartProduct(User user, Product product, int quantity) {
        return user.getCart().getCartProducts().stream()
                .filter(cp -> cp.getProduct().equals(product))
                .findFirst()
                .map(cp -> {
                    cp.setChecked(true);
                    cp.setDeleted(false);
                    cp.updateQuantity(quantity);
                    cp.setCreatedAt(LocalDateTime.now());
                    return cp;
                })
                .orElseGet(() -> {
                    CartProduct newCartProduct = CartProduct.builder()
                            .cart(user.getCart())
                            .product(product)
                            .quantity(quantity)
                            .createdAt(LocalDateTime.now())
                            .checked(true)
                            .deleted(false)
                            .build();
                    user.getCart().getCartProducts().add(newCartProduct);
                    return newCartProduct;
                });
    }

    // 장바구니 상품 찾기
    private CartProduct findCartProduct(User user, Long cartProductId) {
        return user.getCart().getCartProducts().stream()
                .filter(cp -> cp.getId().equals(cartProductId))
                .findFirst().orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_IN_CART));
    }

    // 상품 삭제 표시
    private void markProductAsDeleted(CartProduct cartProduct) {
        cartProduct.setDeleted(true);
    }

    // 장바구니 상품 응답 생성
    private CartAddItemResponse createCartAddItemResponse(CartProduct cartProduct) {
        return CartAddItemResponse.builder()
                .productId(cartProduct.getProduct().getId())
                .productName(cartProduct.getProduct().getName())
                .quantity(cartProduct.getQuantity())
                .checked(cartProduct.isChecked())
                .build();
    }
}


