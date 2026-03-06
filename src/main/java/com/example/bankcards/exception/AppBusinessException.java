package com.example.bankcards.exception;

public abstract class AppBusinessException extends RuntimeException {
    public AppBusinessException(String message) {
        super(message);
    }
}
