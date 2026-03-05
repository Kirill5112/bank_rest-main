package com.example.bankcards.exception;

public class AuthenticationMismatchException extends RuntimeException {
    public AuthenticationMismatchException(){
        super("Anonymous user principal");
    }
}
