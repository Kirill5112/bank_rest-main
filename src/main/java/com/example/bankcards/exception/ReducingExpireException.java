package com.example.bankcards.exception;


public class ReducingExpireException extends AppBusinessException {
    public ReducingExpireException(Long id) {
        super("the expire cannot be reduced, card id: " + id);
    }
}