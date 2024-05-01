package com.ksh.purchase.controller.reqeust;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RefundRequest(
        @Positive(message = "주문 ID는 0보다 커야 합니다.")
        @NotNull(message = "주문 ID는 필수입니다.")
        Long orderId,

        @Positive(message = "주문 상품 ID는 0보다 커야 합니다.")
        @NotNull(message = "주문 상품 ID는 필수입니다.")
        Long orderProductId,

        @NotNull(message = "반품 사유를 입력해 주세요.")
        String reason
) {
}
