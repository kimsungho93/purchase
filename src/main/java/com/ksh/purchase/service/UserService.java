package com.ksh.purchase.service;

import com.ksh.purchase.controller.reqeust.CreateUserRequest;
import com.ksh.purchase.controller.reqeust.LoginRequest;
import com.ksh.purchase.controller.response.LoginResponse;
import com.ksh.purchase.entity.Address;
import com.ksh.purchase.entity.User;
import com.ksh.purchase.exception.CustomException;
import com.ksh.purchase.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final EncryptService encryptService;
    private final RedisService redisService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;


    // 회원가입
    @Transactional
    public Long signup(CreateUserRequest request) {
        User user = encryptService.encryptUser(request.toUserEntity());
        Address address = encryptService.encryptAddress(request.toAddressEntity(user));
        address.setUser(user);

        if (checkDuplicateEmail(user.getEmail())) {
            throw new CustomException("이미 가입된 이메일입니다.", HttpStatus.BAD_REQUEST);
        }

        userRepository.save(user);
        redisService.setValue(String.valueOf(user.getId()), String.valueOf(user.getId()), Duration.ofMinutes(2));
        return user.getId();
    }

    // 로그인
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        // 이메일 검증
        User user = userRepository.findByEmail(encryptService.encrypt(loginRequest.email())).orElseThrow(
                () -> new CustomException("가입되지 않은 이메일입니다.", HttpStatus.NOT_FOUND)
        );
        // 비밀번호 검증
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new CustomException("비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
        }
        // 로그인 성공 시 토큰 발급
        String token = tokenProvider.generateToken(user);
        redisService.setValue(token, "valid", Duration.ofHours(1));
        return new LoginResponse(token);
    }

    // 로그아웃
    @Transactional
    public void logout(String token) {
        redisService.deleteValue(token);
    }

    @Transactional
    public void verifyEmail(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException("가입되지 않은 이메일입니다.", HttpStatus.NOT_FOUND));
        user.setCertificated(true);
        userRepository.save(user);
    }

    // 이메일 중복 체크
    private boolean checkDuplicateEmail(String email) {
        return userRepository.existsByEmail(email);
    }


}

