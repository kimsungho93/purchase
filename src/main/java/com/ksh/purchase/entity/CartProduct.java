package com.ksh.purchase.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart_product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CartProduct implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_product_id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "cart_id")
    @Setter
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    @Setter
    private int quantity;

    @Column(nullable = false, columnDefinition = "boolean default true")
    @Setter
    private boolean checked = true;

    @Column(nullable = false, columnDefinition = "boolean default false")
    @Setter
    private boolean deleted = false;

    @CreatedDate
    private LocalDateTime createdAt;

    public  void updateQuantity(int quantity) {
        this.quantity = quantity;
        this.cart.getCartProducts().add(this);
    }


}
