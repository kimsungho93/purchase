package com.ksh.purchase.event;

import com.ksh.purchase.entity.Order;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OrderStatusChangeEvent extends ApplicationEvent {
    private final Order order;

    public OrderStatusChangeEvent(Object source, Order order) {
        super(source);
        this.order = order;
    }

}
