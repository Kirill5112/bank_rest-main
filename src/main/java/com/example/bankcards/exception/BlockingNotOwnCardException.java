package com.example.bankcards.exception;

public class BlockingNotOwnCardException extends AppBusinessException {
    public BlockingNotOwnCardException() {
        super("Can block only your own cards");
    }
}
