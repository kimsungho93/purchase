package com.ksh.purchase.service;

import com.ksh.purchase.controller.reqeust.*;
import com.ksh.purchase.controller.response.LoginResponse;
import com.ksh.purchase.entity.Address;
import com.ksh.purchase.entity.User;
import com.ksh.purchase.exception.CustomException;
import com.ksh.purchase.exception.ErrorCode;
import com.ksh.purchase.repository.AddressRepository;
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
    private final AddressRepository addressRepository;
    private final EncryptService encryptService;
    private final RedisService redisService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    // 회원가입
    @Transactional
    public Long signup(CreateUserRequest request) {
        validateDuplicateEmail(request.email());
        User user = saveNewUser(request);
        saveNewAddress(user, request);
        return user.getId();
    }

    // 로그인
    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = validateUserCredentials(request.email(), request.password());
        String token = tokenProvider.generateToken(user);
        redisService.setValue(token, "valid", Duration.ofDays(5));  // 토큰 유효 시간 설정
        return new LoginResponse(token);
    }

    // 로그아웃
    @Transactional
    public void logout(String token) {
        redisService.deleteValue(token);
    }

    // 이메일 인증
    @Transactional
    public void verifyEmail(Long id) {
        User user = findUserById(id);
        user.setCertificated(true);
        userRepository.save(user);
    }

    // 주소, 전화번호 변경
    @Transactional
    public void updateAddressAndPhone(Long id, UserInfoUpdateRequest request) {
        User user = findUserById(id);
        updatePhone(user, request.phone());
        updateAddress(request.addressId(), request);
    }

    // 비밀번호 변경
    @Transactional
    public void updatePassword(Long id, PasswordUpdateRequest request) {
        User user = findUserById(id);
        checkPasswordMatches(request, user);
        user.setPassword(passwordEncoder.encode(request.newPassword()));
    }



    // Private helper methods
    private void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmail(encryptService.encrypt(email))) {
            throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
        }
    }

    private User saveNewUser(CreateUserRequest request) {
        User user = encryptService.encryptUser(request.toUserEntity());
        userRepository.save(user);
        redisService.setValue(String.valueOf(user.getId()), String.valueOf(user.getId()), Duration.ofMinutes(2));
        return user;
    }

    private void saveNewAddress(User user, CreateUserRequest request) {
        Address address = encryptService.encryptAddress(request.toAddressEntity(user));
        address.setUser(user);
        addressRepository.save(address);
    }

    private User validateUserCredentials(String email, String password) {
        User user = userRepository.findByEmail(encryptService.encrypt(email))
                .orElseThrow(() -> new CustomException(ErrorCode.MAIL_NOT_FOUND));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
        return user;
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new CustomException("회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
    }

    private void updatePhone(User user, String phone) {
        if (!phone.isEmpty() && !phone.equals(encryptService.decrypt(user.getPhone()))) {
            user.setPhone(encryptService.encrypt(phone));
        }
    }

    private void updateAddress(Long addressId, UserInfoUpdateRequest request) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new CustomException(ErrorCode.ADDRESS_NOT_FOUND));
        if (!request.zipcode().isEmpty()) {
            address.setZipcode(encryptService.encrypt(request.zipcode()));
        }
        if (!request.address().isEmpty()) {
            address.setAddress(encryptService.encrypt(request.address()));
        }
        if (!request.detailedAddress().isEmpty()) {
            address.setDetailedAddress(encryptService.encrypt(request.detailedAddress()));
        }
    }

    private void checkPasswordMatches(PasswordUpdateRequest request, User user) {
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
    }
}
