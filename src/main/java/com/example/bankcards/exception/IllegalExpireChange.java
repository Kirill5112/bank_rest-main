package com.example.bankcards.exception;


public class IllegalExpireChange extends RuntimeException {
    public IllegalExpireChange(Long id) {
        super("Срок действия нельзя уменьшать, card id: " + id);
    }
}