package com.ksh.purchase.entity.enums;

import lombok.Getter;

@Getter
public enum RefundStatus {
    PENDING("보류중"), REFUND_COMPLETE("환불완료");

    private final String description;

    RefundStatus(String description) {
        this.description = description;
    }
}
