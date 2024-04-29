package com.ksh.purchase.controller.reqeust;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemDeleteRequest(
        @NotNull(message = "장바구니 상품 아이디는 필수입니다.")
        @Positive(message = "장바구니 상품 아이디는 1 이상이어야 합니다.")
        Long cartProductId) {
}
