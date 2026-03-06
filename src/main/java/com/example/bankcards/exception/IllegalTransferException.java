package com.example.bankcards.exception;

public class IllegalTransferException extends AppBusinessException {
    public IllegalTransferException() {
        super("Transfers only between your own cards");
    }
}
