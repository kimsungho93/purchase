package com.ksh.purchase.util;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;

@UtilityClass
public class OrderNumberGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int ORDER_NUMBER_LENGTH = 10;

    public static String createOrderNumber() {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < ORDER_NUMBER_LENGTH; i++) {
            result.append(characters.charAt(RANDOM.nextInt(characters.length())));
        }

        return result.toString();
    }

}
