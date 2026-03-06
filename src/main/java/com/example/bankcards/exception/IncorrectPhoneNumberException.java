package com.example.bankcards.exception;

public class IncorrectPhoneNumberException extends AppBusinessException {
    public IncorrectPhoneNumberException(String phone) {
        super("Incorrect phone number: " + phone);
    }
}
