package com.ksh.purchase.controller;

import com.ksh.purchase.controller.reqeust.CreateUserRequest;
import com.ksh.purchase.controller.reqeust.LoginRequest;
import com.ksh.purchase.controller.response.LoginResponse;
import com.ksh.purchase.service.MailService;
import com.ksh.purchase.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MailService mailService;

    // 회원 가입
    @PostMapping
    public void signup(@Valid @RequestBody CreateUserRequest request) {
        Long id = userService.signup(request);
        mailService.sendMail(request.email(), "[Purchase 회원가입 이메일 인증 링크]", id + UUID.randomUUID().toString().substring(0, 7));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }
}
