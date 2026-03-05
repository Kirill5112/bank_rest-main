package com.example.bankcards.exception;

import java.math.BigDecimal;

public class NegativeBalanceException extends RuntimeException {
    public NegativeBalanceException(BigDecimal balance) {
        super("not enough funds: " + balance);
    }
}
