package com.ksh.purchase.entity.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    ORDER_COMPLETE("주문 완료"),
    DELIVERY("배송 중"),
    DELIVERY_COMPLETE("배송 완료");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }
}
