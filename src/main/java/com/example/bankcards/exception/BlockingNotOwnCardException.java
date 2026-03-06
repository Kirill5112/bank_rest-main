package com.example.bankcards.exception;

public class BlockingNotOwnCardException extends RuntimeException {
    public BlockingNotOwnCardException() {
        super("Can block only your own cards");
    }
}
