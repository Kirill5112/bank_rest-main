package com.example.bankcards.exception;


import com.example.bankcards.enums.CardStatus;


public class IllegalBalanceChange extends RuntimeException {
    public IllegalBalanceChange(CardStatus status, Long id) {
        super(status.getDescription() + ",card id: " + id);
    }
    public IllegalBalanceChange(Long id){
        super("Баланс не может быть отрицательным,card id: " + id);
    }
}