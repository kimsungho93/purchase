package com.ksh.purchase.service;

import com.ksh.purchase.controller.reqeust.RefundRequest;
import com.ksh.purchase.entity.*;
import com.ksh.purchase.entity.enums.OrderStatus;
import com.ksh.purchase.exception.CustomException;
import com.ksh.purchase.exception.ErrorCode;
import com.ksh.purchase.repository.RefundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefundService {

    private final RefundRepository refundRepository;
    private final UserService userService;
    private final OrderService orderService;

    // 환불 처리 메인 메서드
    public void processRefund(long userId, RefundRequest request) {
        User user = userService.findById(userId);
        Order order = findOrderById(user, request.orderId());
        OrderProduct orderProduct = findOrderProduct(order, request.orderProductId());

        validateRefundEligibility(order);
        createAndSaveRefund(order, request, orderProduct);
        performRefundActions(order, orderProduct);
    }

    // 주문 찾기
    private Order findOrderById(User user, long orderId) {
        return user.getOrderList().stream()
                .filter(order -> order.getId() == orderId)
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
    }

    // 주문 상품 찾기
    private OrderProduct findOrderProduct(Order order, long productId) {
        return order.getProducts().stream()
                .filter(product -> product.getProduct().getId() == productId)
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_PRODUCT_NOT_FOUND));
    }

    // 환불 가능성 검증
    private void validateRefundEligibility(Order order) {
        if (order.getStatus() != OrderStatus.DELIVERY_COMPLETE || Duration.between(order.getUpdatedAt(), LocalDateTime.now()).toDays() >= 1) {
            throw new CustomException(ErrorCode.ORDER_CANNOT_REFUND);
        }
    }

    // 환불 생성 및 저장
    private void createAndSaveRefund(Order order, RefundRequest request, OrderProduct orderProduct) {
        Refund refund = Refund.createRefund(order, request);
        RefundProduct refundProduct = RefundProduct.createRefundProduct(refund, orderProduct);
        refund.getReturnProducts().add(refundProduct);
        refundRepository.save(refund);
    }

    // 환불 처리 후 작업 수행
    private void performRefundActions(Order order, OrderProduct orderProduct) {
        order.refund(orderProduct);
        orderService.save(order);
    }
}
