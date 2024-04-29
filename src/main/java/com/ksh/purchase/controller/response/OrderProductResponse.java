package com.ksh.purchase.controller.response;

import com.ksh.purchase.entity.OrderProduct;
import lombok.Builder;
import lombok.Getter;

import java.text.NumberFormat;
import java.util.Locale;

@Getter
@Builder
public class OrderProductResponse {
    private String productName; // 상품명
    private String price; // 가격
    private int quantity; // 수량

    public static OrderProductResponse from(OrderProduct orderProduct) {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.KOREA);
        return OrderProductResponse.builder()
                .productName(orderProduct.getProduct().getName())
                .price(numberFormat.format(orderProduct.getProduct().getPrice()) + "원")
                .quantity(orderProduct.getQuantity())
                .build();
    }
}
