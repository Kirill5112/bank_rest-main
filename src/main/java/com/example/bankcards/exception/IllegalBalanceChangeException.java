package com.example.bankcards.exception;


import com.example.bankcards.enums.CardStatus;


public class IllegalBalanceChangeException extends RuntimeException {
    public IllegalBalanceChangeException(CardStatus status, Long id) {
        super(status.toString() + ", card id: " + id);
    }
}