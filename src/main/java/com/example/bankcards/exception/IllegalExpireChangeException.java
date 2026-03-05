package com.example.bankcards.exception;


public class IllegalExpireChangeException extends RuntimeException {
    public IllegalExpireChangeException(Long id) {
        super("the expire cannot be reduced, card id: " + id);
    }
}