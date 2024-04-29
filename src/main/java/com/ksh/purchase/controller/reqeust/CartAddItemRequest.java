package com.ksh.purchase.controller.reqeust;

import jakarta.validation.constraints.*;

public record CartAddItemRequest(
        @Positive(message = "상품 아이디는 1 이상이어야 합니다.")
        @NotNull(message = "상품 아이디는 필수입니다.")
        Long productId,

        @NotNull(message = "수량은 필수입니다.")
        @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
        @Max(value = 100, message = "수량은 100개 이하이어야 합니다.")
        Integer quantity
) {
}
