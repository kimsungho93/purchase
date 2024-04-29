package com.ksh.purchase.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartAddItemResponse {
    private final Long productId;
    private final String productName;
    private final int quantity;
    private final boolean checked;
}
