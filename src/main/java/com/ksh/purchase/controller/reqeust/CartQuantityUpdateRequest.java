package com.ksh.purchase.controller.reqeust;

import jakarta.validation.constraints.*;

public record CartQuantityUpdateRequest(

        @Positive(message = "상품 아이디는 1 이상이어야 합니다.")
        @NotNull
        Long cartProductId,

        @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
        @Max(value = 100, message = "수량은 100개 이하이어야 합니다.")
        @NotNull
        Integer quantity
) {
}
