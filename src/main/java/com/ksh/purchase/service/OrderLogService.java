package com.ksh.purchase.service;

import com.ksh.purchase.entity.Order;
import com.ksh.purchase.entity.OrderLog;
import com.ksh.purchase.repository.OrderLogRepository;
import com.ksh.purchase.event.OrderStatusChangeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderLogService {
    private final OrderLogRepository orderLogRepository;

    @EventListener
    public void handleOrderStatusChange(OrderStatusChangeEvent event) {
        Order order = event.getOrder();
        OrderLog orderLog = OrderLog.createOrderLog(order);
        orderLogRepository.save(orderLog);
    }
}
