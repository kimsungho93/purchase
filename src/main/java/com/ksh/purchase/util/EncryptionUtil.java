package com.ksh.purchase.util;

import com.ksh.purchase.entity.Address;
import com.ksh.purchase.entity.User;
import com.ksh.purchase.exception.CustomException;
import com.ksh.purchase.exception.ErrorCode;
import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.function.Function;


@UtilityClass
public class EncryptionUtil {

    private static final String ALGORITHM = "AES";
    private static final byte[] KEY = "ThisIsASecretKey".getBytes();
    public static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 데이터 암호화 메소드
    public static String encrypt(String inputData) {
        try {
            SecretKey secretKey = new SecretKeySpec(KEY, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] cipherText = cipher.doFinal(inputData.getBytes());
            return Base64.getEncoder().encodeToString(cipherText);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.ENCRYPTION_FAIL);
        }
    }

    // 데이터 복호화 메소드
    public static String decrypt(String encryptedData) {
        try {
            SecretKey secretKey = new SecretKeySpec(KEY, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(plainText);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.DECRYPTION_FAIL);
        }
    }

    private static <T> T encryptEntity(T entity, Function<T, T> encryptFunction) {
        return encryptFunction.apply(entity);
    }

    public static User encryptUser(User user) {
        return encryptEntity(user, u -> User.builder()
                .name(encrypt(u.getName()))
                .email(encrypt(u.getEmail()))
                .password(passwordEncoder.encode(u.getPassword()))
                .phone(encrypt(u.getPhone()))
                .userType(u.getUserType())
                .addressList(new ArrayList<>())
                .build());
    }

    public static Address encryptAddress(Address address) {
        return encryptEntity(address, a -> Address.builder()
                .zipcode(encrypt(a.getZipcode()))
                .address(encrypt(a.getAddress()))
                .detailedAddress(encrypt(a.getDetailedAddress()))
                .build());
    }

}
