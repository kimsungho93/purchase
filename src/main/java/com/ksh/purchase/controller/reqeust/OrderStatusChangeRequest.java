package com.ksh.purchase.controller.reqeust;

import com.ksh.purchase.util.ValidOrderStatus;

public record OrderStatusChangeRequest(
        @ValidOrderStatus
        String status
) {
}
