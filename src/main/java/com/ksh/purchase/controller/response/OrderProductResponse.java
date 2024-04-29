package com.ksh.purchase.controller.response;

import com.ksh.purchase.entity.OrderProduct;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderProductResponse {
    private String productName; // 상품명
    private int price; // 가격
    private int quantity; // 수량

    public static OrderProductResponse from(OrderProduct orderProduct) {
        return OrderProductResponse.builder()
                .productName(orderProduct.getProduct().getName())
                .price(orderProduct.getProduct().getPrice())
                .quantity(orderProduct.getQuantity())
                .build();
    }
}
