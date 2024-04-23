package com.ksh.purchase.entity.enums;

import lombok.Getter;

@Getter
public enum ProductStatus {
    //판매중, 품절
    SALE("판매중"), SOLD_OUT("품절");

    private final String description;

    ProductStatus(String description) {
        this.description = description;
    }
}
