package com.ksh.purchase.controller;

import com.ksh.purchase.exception.CustomException;
import com.ksh.purchase.service.RedisService;
import com.ksh.purchase.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final UserService userService;
    private final RedisService redisService;


    @GetMapping("/email/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String email) {
        String key = String.valueOf(email.charAt(0));

        if (redisService.getValue(key) == null) {
            throw new CustomException("인증 시간이 만료되었습니다.", HttpStatus.BAD_REQUEST);
        }

        userService.verifyEmail(Long.parseLong(key));
        return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
    }

}
