package com.ksh.purchase.controller.response;

import com.ksh.purchase.entity.Product;
import com.ksh.purchase.util.EncryptionUtil;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductResponse {
    private String name;
    private int price;
    private String description;
    private String userName;
    private String status;


    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .userName(EncryptionUtil.decrypt(product.getUser().getName()))
                .status(product.getStatus().getDescription())
                .build();
    }
}
