package com.ksh.purchase.entity;

import com.ksh.purchase.controller.reqeust.RefundRequest;
import com.ksh.purchase.entity.enums.RefundStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "refund")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Refund {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refund_id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY ,cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @OneToMany(mappedBy = "refund", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefundProduct> returnProducts;

    @Enumerated(EnumType.STRING)
    private RefundStatus status;

    @Column(nullable = false)
    private String reason;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    public static Refund createRefund(Order order, RefundRequest request) {
        return Refund.builder()
                .order(order)
                .returnProducts(new ArrayList<>())
                .status(RefundStatus.PENDING)
                .reason(request.reason())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
