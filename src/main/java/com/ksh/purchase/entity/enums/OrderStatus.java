package com.ksh.purchase.entity.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    BEFORE_DELIVERY("배송 전"),
    DELIVERY("배송 중"),
    COMPLETE("배송 완료");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }
}
