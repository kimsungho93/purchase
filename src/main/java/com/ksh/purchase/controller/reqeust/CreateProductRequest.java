package com.ksh.purchase.controller.reqeust;

import com.ksh.purchase.entity.Product;
import com.ksh.purchase.entity.User;
import com.ksh.purchase.entity.enums.ProductStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateProductRequest(
        @Size(max = 100, message = "상품명은 100자 이하로 입력해주세요.")
        @NotBlank(message = "상품명을 입력해주세요.")
        String name,

        @Min(value = 1000, message = "상품 가격은 1,000원 이상으로 입력해주세요.")
        @Max(value = 100000000, message = "상품 가격은 100,000,000원 이하로 입력해주세요.")
        Integer price,

        @Min(value = 1, message = "상품 재고는 1 이상으로 입력해주세요.")
        @Max(value = 1000, message = "상품 재고는 1,000 이하로 입력해주세요.")
        Integer stock,

        @NotBlank(message = "상품 설명을 입력해주세요.")
        @Size(max = 500, message = "상품 설명은 500자 이하로 입력해주세요.")
        String description
) {
    public Product toEntity(CreateProductRequest request, User user) {
        return Product.builder()
                .name(request.name())
                .price(request.price())
                .stock(request.stock())
                .description(request.description())
                .user(user)
                .status(ProductStatus.SALE)
                .build();
    }
}
