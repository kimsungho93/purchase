package com.ksh.purchase.service;

import com.ksh.purchase.entity.Address;
import com.ksh.purchase.entity.User;
import com.ksh.purchase.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.function.Function;


@Service
@RequiredArgsConstructor
public class EncryptService {

    private static final String ALGORITHM = "AES";
    private static final byte[] KEY = "ThisIsASecretKey".getBytes();
    private final PasswordEncoder passwordEncoder;

    // 데이터 암호화 메소드
    public String encrypt(String inputData) {
        try {
            SecretKey secretKey = new SecretKeySpec(KEY, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] cipherText = cipher.doFinal(inputData.getBytes());
            return Base64.getEncoder().encodeToString(cipherText);
        } catch (Exception e) {
            throw new CustomException("Failed to encrypt data", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 데이터 복호화 메소드
    public String decrypt(String encryptedData) {
        try {
            SecretKey secretKey = new SecretKeySpec(KEY, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(plainText);
        } catch (Exception e) {
            throw new CustomException("Failed to encrypt data", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private <T> T encryptEntity(T entity, Function<T, T> encryptFunction) {
        return encryptFunction.apply(entity);
    }

    public User
    encryptUser(User user) {
        return encryptEntity(user, u -> User.builder()
                .name(encrypt(u.getName()))
                .email(encrypt(u.getEmail()))
                .password(passwordEncoder.encode(u.getPassword()))
                .phone(encrypt(u.getPhone()))
                .userType(u.getUserType())
                .addressList(new ArrayList<>())
                .build());
    }

    public Address encryptAddress(Address address) {
        return encryptEntity(address, a -> Address.builder()
                .zipcode(encrypt(a.getZipcode()))
                .address(encrypt(a.getAddress()))
                .detailedAddress(encrypt(a.getDetailedAddress()))
                .build());
    }

}
