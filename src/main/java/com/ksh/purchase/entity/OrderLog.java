package com.ksh.purchase.entity;

import com.ksh.purchase.entity.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderLog {
    @Id
    @GeneratedValue
    @Column(name = "order_log_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime createdAt;

    public static OrderLog createOrderLog(Order order) {
        OrderLog orderLog = OrderLog.builder()
                .order(order)
                .status(order.getStatus())
                .createdAt(LocalDateTime.now())
                .build();
        orderLog.getOrder().getOrderLogs().add(orderLog);
        return orderLog;
    }
}
