package com.ksh.purchase.controller;

import com.ksh.purchase.controller.reqeust.RefundRequest;
import com.ksh.purchase.service.RefundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RefundController {
    private final RefundService refundService;

    @PostMapping("/api/v1/refunds")
    public void refund(@AuthenticationPrincipal User user, @Valid @RequestBody RefundRequest request) {
        long userId = Long.parseLong(user.getUsername());
        refundService.processRefund(userId, request);
    }
}
