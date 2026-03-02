package com.example.bankcards.util;

//@formatter:off
public record CardNumberData(
        String maskedNumber,
        String numberLast4,
        byte[] numberHash,
        String fullNumber
) {}
