package com.ksh.purchase.service;

import com.ksh.purchase.controller.reqeust.CreateOrderRequest;
import com.ksh.purchase.controller.response.OrderCancelResponse;
import com.ksh.purchase.controller.response.OrderResponse;
import com.ksh.purchase.entity.Order;
import com.ksh.purchase.entity.OrderProduct;
import com.ksh.purchase.entity.Product;
import com.ksh.purchase.entity.User;
import com.ksh.purchase.entity.enums.OrderStatus;
import com.ksh.purchase.exception.CustomException;
import com.ksh.purchase.exception.ErrorCode;
import com.ksh.purchase.repository.OrderRepository;
import com.ksh.purchase.event.OrderStatusChangeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final UserService userService;
    private final ProductService productService;
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    // 주문 하기
    @Transactional
    public OrderResponse createOrder(long userId, List<CreateOrderRequest> request) {
        User user = userService.findById(userId);
        List<OrderProduct> orderProducts = new ArrayList<>();

        for (CreateOrderRequest dto : request) {
            Product product = productService.findBtyId(dto.productId());
            product.minusStock(dto.quantity());
            OrderProduct orderProduct1 = OrderProduct.createOrderProduct(product, dto.quantity());
            orderProducts.add(orderProduct1);
        }

        Order saved = orderRepository.save(Order.createOrder(user, orderProducts));
        orderProducts.forEach(orderProduct -> orderProduct.setOrder(saved));
        eventPublisher.publishEvent(new OrderStatusChangeEvent(this, saved));
        return OrderResponse.from(saved);
    }

    // 주문 취소
    @Transactional
    public OrderCancelResponse cancelOrder(long userId, long orderId) {
        User user = userService.findById(userId);
        Order order = user.getOrderList().stream()
                .filter(o -> o.getId() == orderId)
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
        order.cancel();
        eventPublisher.publishEvent(new OrderStatusChangeEvent(this, order));
        return OrderCancelResponse.of(order);
    }

    // 주문 목록 조회(회원 아이디)
    public List<OrderResponse> getOrders(long userId) {
        User user = userService.findById(userId);
        return user.getOrderList().stream()
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .map(OrderResponse::from)
                .collect(Collectors.toList());
    }

    // 주문 목록 조회(주문 상태)
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return Optional.ofNullable(orderRepository.findByStatus(status))
                .filter(orders -> !orders.isEmpty())
                .orElse(new ArrayList<>());
    }

    // 주문 상태 변경
    @Transactional
    public void changeOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
        order.setStatus(newStatus);
        orderRepository.save(order);
        eventPublisher.publishEvent(new OrderStatusChangeEvent(this, order));
    }

    // 주문 상태 변경
    @Transactional
    public void changeOrderStatus(long userId, Long orderId, OrderStatus newStatus) {
        User user = userService.findById(userId);
        Order order = user.getOrderList().stream()
                .filter(o -> o.getId() == orderId)
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
        order.setStatus(newStatus);
        orderRepository.save(order);
        eventPublisher.publishEvent(new OrderStatusChangeEvent(this, order));
    }

    public void saveAll(List<Order> orders) {
        orderRepository.saveAll(orders);
    }

}
