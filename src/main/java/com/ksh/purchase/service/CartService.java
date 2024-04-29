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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final UserService userService;
    private final ProductService productService;

    // 장바구니에 상품 추가
    public CartAddItemResponse addItemtoCart(String userId, CartAddItemRequest request) {
        User user = userService.findById(Long.parseLong(userId));
        Product product = productService.findBtyId(request.productId());
        // 장바구니에 이미 있는 상품인지 확인
        user.getCart().getCartProducts().forEach(cartProduct -> {
            if (cartProduct.getProduct().getId().equals(request.productId())) {
                cartProduct.setQuantity(request.quantity());
                user.getCart().addCartProduct(cartProduct);
                userService.save(user);
                throw new CustomException(ErrorCode.CART_ALREADY_EXIST);
            }
        });

        CartProduct cartProduct = CartProduct.builder()
                .cart(user.getCart())
                .product(product)
                .quantity(request.quantity())
                .checked(true)
                .createdAt(LocalDateTime.now())
                .build();

        user.getCart().addCartProduct(cartProduct);
        userService.save(user);

        return CartAddItemResponse.builder()
                .productId(product.getId())
                .productName(product.getName())
                .quantity(request.quantity())
                .checked(true)
                .build();
    }

    // 장바구니 조회
    public Set<CartProductResponse> getCart(String userId) {
        User user = userService.findById(Long.parseLong(userId));
        Set<CartProduct> cartProducts = user.getCart().getCartProducts();

        TreeSet<CartProductResponse> cartProductResponses = new TreeSet<>(Comparator.comparing(CartProductResponse::getCreatedAt).reversed());
        cartProducts.forEach(cartProduct -> {
            cartProductResponses.add(CartProductResponse.of(cartProduct));
        });
        return cartProductResponses;
    }

    // 카트에 있는 상품 수량 변경
    public CartProductResponse updateQuantity(String userId, CartQuantityUpdateRequest request) {
        User user = userService.findById(Long.parseLong(userId));
        Set<CartProduct> cartProducts = user.getCart().getCartProducts();
        for (CartProduct cartProduct : cartProducts) {
            if (cartProduct.getId().equals(request.cartProductId())) {
                cartProduct.updateQuantity(request.quantity());
                userService.save(user);
                return CartProductResponse.of(cartProduct);
            }
        }
        throw new CustomException(ErrorCode.PRODUCT_NOT_IN_CART);
    }
}
