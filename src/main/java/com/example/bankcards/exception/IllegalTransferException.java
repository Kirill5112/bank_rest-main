package com.example.bankcards.exception;

public class IllegalTransferException extends RuntimeException {
    public IllegalTransferException() {
        super("Transfers only between your own cards");
    }
}
