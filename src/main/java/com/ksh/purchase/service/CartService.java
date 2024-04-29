package com.ksh.purchase.service;

import com.ksh.purchase.controller.reqeust.CartAddItemRequest;
import com.ksh.purchase.controller.reqeust.CartItemDeleteRequest;
import com.ksh.purchase.controller.reqeust.CartProductIdRequest;
import com.ksh.purchase.controller.reqeust.CartQuantityUpdateRequest;
import com.ksh.purchase.controller.response.CartAddItemResponse;
import com.ksh.purchase.controller.response.CartProductResponse;
import com.ksh.purchase.entity.CartProduct;
import com.ksh.purchase.entity.Product;
import com.ksh.purchase.entity.User;
import com.ksh.purchase.exception.CustomException;
import com.ksh.purchase.exception.ErrorCode;
import com.ksh.purchase.repository.UserRepository;
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
    private final UserRepository userRepository; // 이 부분이 추가되었습니다.

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
                .deleted(false)
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

        TreeSet<CartProductResponse> cartProductResponses = cartProducts.stream()
                .filter(cartProduct -> !cartProduct.isDeleted())
                .map(CartProductResponse::of)
                .collect(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator.comparing(CartProductResponse::getCreatedAt).reversed())
                ));
        return cartProductResponses;
    }

    // 장바구니에서 상품 단 건 삭제
    public void deleteItemFromCart(String userId, Long cartProductId) {
        User user = userService.findById(Long.parseLong(userId));
        Set<CartProduct> cartProducts = user.getCart().getCartProducts();
        for (CartProduct cartProduct : cartProducts) {
            if (cartProduct.getId().equals(cartProductId)) {
                cartProduct.setDeleted(true);
                userService.save(user);
                return;
            }
        }
        throw new CustomException(ErrorCode.PRODUCT_NOT_IN_CART);
    }

    // 장바구니에서 선택된 상품만 삭제
    public void deleteCheckedItemsFromCart(String userId, List<CartItemDeleteRequest> cartProductIds) {
        User user = userService.findById(Long.parseLong(userId));
        Set<CartProduct> cartProducts = user.getCart().getCartProducts();

        cartProductIds.stream()
                .map(CartItemDeleteRequest::cartProductId)
                .forEach(cartProductId -> {
                    cartProducts.stream()
                            .filter(cartProduct -> cartProduct.getId().equals(cartProductId) && cartProduct.isChecked())
                            .forEach(cartProduct -> {
                                cartProduct.setDeleted(true);
                                user.getCart().addCartProduct(cartProduct);
                            });
                });
        userService.save(user);
    }

    // 장바구니에서 상품 전체 삭제
    public void deleteAllItemsFromCart(String userId) {
        User user = userService.findById(Long.parseLong(userId));
        Set<CartProduct> cartProducts = user.getCart().getCartProducts();
        if (cartProducts.isEmpty()) {
            throw new CustomException(ErrorCode.CART_IS_EMPTY);
        }
        cartProducts.forEach(cartProduct -> cartProduct.setDeleted(true));
        userService.save(user);
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

    // 장바구니에서 상품 선택 바꾸기
    public void checkItemFromCart(String userId, List<CartProductIdRequest> cartProductIds) {
        User user = userService.findById(Long.parseLong(userId));
        Set<CartProduct> cartProducts = user.getCart().getCartProducts();
        for (CartProductIdRequest cartProductId : cartProductIds) {
            for (CartProduct cartProduct : cartProducts) {
                if (cartProduct.getId().equals(cartProductId.cartProductId())) {
                    cartProduct.setChecked(!cartProduct.isChecked());
                    user.getCart().addCartProduct(cartProduct);
                }
            }
        }
        userService.save(user);
    }
}
