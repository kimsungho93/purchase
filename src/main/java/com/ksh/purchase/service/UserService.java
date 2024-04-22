package com.ksh.purchase.service;

import com.ksh.purchase.controller.reqeust.CreateUserRequest;
import com.ksh.purchase.entity.Address;
import com.ksh.purchase.entity.User;
import com.ksh.purchase.exception.CustomException;
import com.ksh.purchase.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    @Transactional
    public void verifyEmail(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException("가입되지 않은 이메일입니다.", HttpStatus.NOT_FOUND));
        user.setCertificated(true);
        userRepository.save(user);
    }

    private boolean checkDuplicateEmail(String email) {
        return userRepository.existsByEmail(email);
    }


}

