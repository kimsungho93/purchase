package com.ksh.purchase.controller.response;

import com.ksh.purchase.entity.Product;
import com.ksh.purchase.util.EncryptionUtil;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private int price;
    private String description;
    private String userName;
    private int stock;
    private String status;


    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .userName(EncryptionUtil.decrypt(product.getUser().getName()))
                .stock(product.getStock())
                .status(product.getStatus().getDescription())
                .build();
    }
}
