package com.example.bankcards.exception;

public class AuthenticationMismatchException extends AppBusinessException {
    public AuthenticationMismatchException(){
        super("Anonymous user principal");
    }
}
