package com.ksh.purchase.entity;

import com.ksh.purchase.entity.enums.OrderStatus;
import com.ksh.purchase.exception.CustomException;
import com.ksh.purchase.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.ksh.purchase.entity.enums.OrderStatus.*;
import static com.ksh.purchase.util.OrderNumberGenerator.createOrderNumber;


@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_order_status", columnList = "status")})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Order extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", updatable = false)
    private Long id;

    @Column(nullable = false)
    private String orderNumber; // 주문번호

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 주문자

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderProduct> products = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address; // 배송지

    @Column(nullable = false)
    private int totalPrice;

    @Enumerated(EnumType.STRING)
    @Setter
    private OrderStatus status;

    public static Order createOrder(User user, List<OrderProduct> orderProducts) {
        Order order = Order.builder()
                .orderNumber(createOrderNumber()) // 주문번호
                .user(user)
                .products(new ArrayList<>(orderProducts))
                .address(getAddress(user))
                .totalPrice(getTotalPrice(orderProducts)) // 총 주문 금액
                .status(ORDER_COMPLETE) // 주문 완료
                .build();
        order.getUser().getOrderList().add(order);
        return order;
    }

    public void cancel() {
        if (this.status == CANCEL) {
            throw new CustomException(ErrorCode.ORDER_ALREADY_CANCEL);
        }
        // 취소 가능한 상태인지 확인
        if (this.status == DELIVERY || this.status == DELIVERY_COMPLETE) {
            throw new CustomException(ErrorCode.ORDER_CANNOT_CANCEL);
        }
        // 취소 가능하면 취소 후 재고 증가
        this.status = CANCEL;
        this.products.forEach(OrderProduct::cancel);
    }

    private static int getTotalPrice (List<OrderProduct> orderProducts) {
        return orderProducts.stream()
                .mapToInt(orderProduct -> orderProduct.getProduct()
                        .getPrice() * orderProduct.getQuantity()).sum();
    }

    private static Address getAddress (User user) {
        return user.getAddressList().stream().filter(Address::isSelected).findFirst().orElseThrow();
    }

}
