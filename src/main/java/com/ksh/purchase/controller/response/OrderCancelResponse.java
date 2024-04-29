package com.ksh.purchase.controller.response;

import com.ksh.purchase.entity.Order;
import lombok.Builder;
import lombok.Getter;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Getter
@Builder
public class OrderCancelResponse {
    private final String orderNumber; // 주문 번호
    private final String orderer; // 주문자
    List<OrderProductResponse> orderProducts; // 주문 상품 목록
    private final String totalPrice; // 총 가격
    private final String status; // 주문 상태
    private final String address; // 배송지
    private final String orderDate; // 주문일
    private final String cancelDate; // 취소일

    public static OrderCancelResponse of(Order order) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.KOREA);
        return OrderCancelResponse.builder()
                .orderNumber(order.getOrderNumber())
                .orderer(order.getUser().getName())
                .orderProducts(order.getProducts().stream()
                        .map(OrderProductResponse::from)
                        .toList())
                .totalPrice(numberFormat.format(order.getTotalPrice()) + "원")
                .status(order.getStatus().getDescription())
                .address(order.getAddress().getFullAddress())
                .orderDate(formatter.format(order.getCreatedAt()))
                .cancelDate(formatter.format(LocalDateTime.now()))
                .build();
    }
}
