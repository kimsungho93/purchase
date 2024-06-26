package com.ksh.purchase.entity;

import com.ksh.purchase.entity.enums.ProductStatus;
import com.ksh.purchase.exception.CustomException;
import com.ksh.purchase.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Product extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", updatable = false)
    private Long id;
    @Column(name = "product_name", nullable = false)
    private String name;
    @Column(nullable = false)
    private int price;
    @Column(nullable = false, columnDefinition = "int default 0")
    private int stock;
    @Column(length = 500, nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(20) default 'SALE'")
    private ProductStatus status;

    public void setUser(User user) {
        this.user = user;
        this.getUser().getProductList().add(this);
    }

    // 재고 차감
    public void minusStock(int quantity) {
        int restStock = this.stock - quantity;
        if (restStock < 0) {
            throw new CustomException(ErrorCode.PRODUCT_STOCK_LACK);
        }
        this.stock = restStock;
    }

    // 재고 증가
    public void plusStock(int count) {
        this.stock += count;
    }
}
