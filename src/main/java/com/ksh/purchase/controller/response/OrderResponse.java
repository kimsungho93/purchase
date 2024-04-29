package com.ksh.purchase.controller.response;


import com.ksh.purchase.entity.Order;
import lombok.Builder;
import lombok.Getter;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static com.ksh.purchase.util.EncryptionUtil.*;

@Builder
@Getter
public class OrderResponse {
    private String orderNumber; // 주문 번호
    private String orderer; // 주문자
    private List<OrderProductResponse> products; // 주문 상품
    private String address; // 배송지
    private String totalPrice; // 총 주문 금액
    private String status; // 주문 상태
    private String createdAt; // 주문일

    public static OrderResponse from(Order order) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.KOREA);
        return OrderResponse.builder()
                .orderNumber(order.getOrderNumber())
                .orderer(decrypt(order.getUser().getName()))
                .products(order.getProducts().stream().map(OrderProductResponse::from).toList())
                .address(order.getAddress().getFullAddress())
                .totalPrice(numberFormat.format(order.getTotalPrice()) + "원")
                .status(order.getStatus().getDescription())
                .createdAt(order.getCreatedAt().format(formatter))
                .build();
    }
}
