package com.ksh.purchase.controller;

import com.ksh.purchase.controller.reqeust.CreateUserRequest;
import com.ksh.purchase.controller.reqeust.LoginRequest;
import com.ksh.purchase.controller.reqeust.PasswordUpdateRequest;
import com.ksh.purchase.controller.reqeust.UserInfoUpdateRequest;
import com.ksh.purchase.controller.response.LoginResponse;
import com.ksh.purchase.exception.CustomException;
import com.ksh.purchase.exception.ErrorCode;
import com.ksh.purchase.service.MailService;
import com.ksh.purchase.service.TokenProvider;
import com.ksh.purchase.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final MailService mailService;
    private final TokenProvider tokenProvider;

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

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        token = token.substring("Bearer ".length());
        userService.logout(token);
        return ResponseEntity.ok().build();
    }

    // 주소, 전화번호 변경
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateAddressAndPhone(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody UserInfoUpdateRequest request) {
        userService.updateAddressAndPhone(id, request);
        return ResponseEntity.ok().build();
    }

    // 비밀번호 변경
    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @Valid @RequestBody PasswordUpdateRequest request) {
        checkToken(token, id);
        userService.updatePassword(id, request);
        return ResponseEntity.ok().build();
    }

    private void checkToken(String token, Long id) {
        Long userIdFromToken = tokenProvider.getUserIdFromToken(token.substring("Bearer ".length()));
        if (!userIdFromToken.equals(id)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

}
