package com.ksh.purchase.service;

import com.ksh.purchase.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ksh.purchase.entity.enums.OrderStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderScheduleService {

    private final OrderService orderService;

    @Scheduled(fixedRate =  1000 * 60 * 60 * 2, initialDelay = 1000 * 60) // 2시간마다 실행
    public void updateOrderStatusToDelivery() {
        log.info("배송 중 상태 변경");
        LocalDateTime now = LocalDateTime.now();
        List<Order> ordersToBeDelivered = orderService.getOrdersByStatus(ORDER_COMPLETE);
        List<Order> ordersUpdatedToDelivery = updateOrdersToDelivery(ordersToBeDelivered, now);
        saveUpdatedOrders(ordersUpdatedToDelivery);
    }

    @Scheduled(fixedRate =  1000 * 60 * 60 * 2, initialDelay = 1000 * 60) // 2시간마다 실행
    public void updateOrderStatusToDeliveryComplete() {
        log.info("배송 완료 상태 변경");
        LocalDateTime now = LocalDateTime.now();
        List<Order> ordersToBeCompleted = orderService.getOrdersByStatus(DELIVERY);
        List<Order> ordersUpdatedToDeliveryComplete = updateOrdersToDeliveryComplete(ordersToBeCompleted, now);
        saveUpdatedOrders(ordersUpdatedToDeliveryComplete);
    }

    private List<Order> updateOrdersToDelivery(List<Order> orders, LocalDateTime currentTime) {
        return orders.stream()
                .filter(order -> Duration.between(order.getCreatedAt(), currentTime).toDays() >= 1)
                .peek(order -> order.setStatus(DELIVERY))
                .collect(Collectors.toList());
    }

    private List<Order> updateOrdersToDeliveryComplete(List<Order> orders, LocalDateTime currentTime) {
        return orders.stream()
                .filter(order -> Duration.between(order.getCreatedAt(), currentTime).toDays() >= 2)
                .peek(order -> order.setStatus(DELIVERY_COMPLETE))
                .collect(Collectors.toList());
    }

    private void saveUpdatedOrders(List<Order> updatedOrders) {
        Optional.ofNullable(updatedOrders)
                .filter(uOrders -> !uOrders.isEmpty())
                .ifPresent(uOrders -> {
                    log.info("상태가 변경된 주문 개수: {}", uOrders.size());
                    orderService.saveAll(uOrders);
                });
    }
}