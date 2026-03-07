package com.example.bankcards.exception;

public class AccessDeniedException extends AppBusinessException{
    public AccessDeniedException() {
        super("Access denied");
    }
}
