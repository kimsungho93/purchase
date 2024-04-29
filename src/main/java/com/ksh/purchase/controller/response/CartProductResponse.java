package com.ksh.purchase.controller.response;

import com.ksh.purchase.entity.CartProduct;
import lombok.Builder;
import lombok.Getter;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Getter
@Builder
public class CartProductResponse {
    private Long cartProductId;
    private String productName;
    private String price;
    private int quantity;
    private boolean checked;
    private String createdAt;

    public static CartProductResponse of(CartProduct cartProduct) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.KOREA);
        return CartProductResponse.builder()
                .cartProductId(cartProduct.getId())
                .productName(cartProduct.getProduct().getName())
                .price(numberFormat.format(cartProduct.getProduct().getPrice()) + "원")
                .quantity(cartProduct.getQuantity())
                .checked(cartProduct.isChecked())
                .createdAt(formatter.format(cartProduct.getCreatedAt()))
                .build();
    }
}
