package com.ksh.purchase.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "refund_product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RefundProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refund_product_id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "refund_id", nullable = false)
    private Refund refund;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "order_product_id", nullable = false)
    private OrderProduct orderProduct;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public static RefundProduct createRefundProduct(Refund refund, OrderProduct orderProduct) {
        return RefundProduct.builder()
                .refund(refund)
                .orderProduct(orderProduct)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
