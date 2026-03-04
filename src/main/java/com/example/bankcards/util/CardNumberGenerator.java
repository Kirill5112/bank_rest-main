package com.example.bankcards.util;


import com.example.bankcards.entity.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class CardNumberGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String MASK_PATTERN = "**** **** **** ";

    public static CardNumberData generate() {
        StringBuilder fullNumber = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            fullNumber.append(RANDOM.nextInt(10));
        }

        String last4 = fullNumber.substring(12);
        String masked = MASK_PATTERN + last4;

        byte[] hash = generateSha256(fullNumber.toString());

        return new CardNumberData(masked, last4, hash, fullNumber.toString());
    }

    private static byte[] generateSha256(String cardNumber) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(cardNumber.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 не доступен", e);
        }
    }

    public static boolean verify(String candidateNumber, byte[] storedHash) {
        byte[] candidateHash = generateSha256(candidateNumber);
        return MessageDigest.isEqual(candidateHash, storedHash);
    }
}

