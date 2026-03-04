package com.example.bankcards.util;

import java.util.regex.Pattern;

public class PhoneNormalizer {

    private static final Pattern pattern = Pattern.compile("^7[0-9]{10}$");

    public static String normalizePhone(String phone) {
        String normPhone = phone.replaceAll("[^0-9]", "")
                .replaceFirst("^8", "7")   // 8→7
                .replaceFirst("^\\d{10}$", "7$1");
        if (!pattern.matcher(normPhone).matches())
            throw new IllegalArgumentException("Incorrect phone number: " + phone);
        return normPhone;
    }
}
