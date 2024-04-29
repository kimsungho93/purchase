package com.ksh.purchase.service;

import com.ksh.purchase.controller.reqeust.CreateOrderRequest;
import com.ksh.purchase.controller.response.OrderResponse;
import com.ksh.purchase.entity.Order;
import com.ksh.purchase.entity.OrderProduct;
import com.ksh.purchase.entity.Product;
import com.ksh.purchase.entity.User;
import com.ksh.purchase.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final UserService userService;
    private final ProductService productService;
    private final OrderRepository orderRepository;

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
        return OrderResponse.from(saved);
    }

    // 주문 목록 조회
    public List<OrderResponse> getOrders(long userId) {
        User user = userService.findById(userId);
        return user.getOrderList().stream()
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .map(OrderResponse::from)
                .collect(Collectors.toList());
    }
}
