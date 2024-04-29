package com.ksh.purchase.controller.reqeust;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
        @NotNull(message = "상품을 입력해주세요")
        Long productId,

        @Min(value = 1, message = "최소 1개 이상 입력해주세요.")
        @Max(value = 100, message = "최대 100개 이하 입력해주세요.")
        int quantity
) {
}
