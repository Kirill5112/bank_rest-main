package com.example.bankcards.exception;

import java.math.BigDecimal;

public class NegativeBalanceException extends AppBusinessException {
    public NegativeBalanceException(BigDecimal balance) {
        super("not enough funds: " + balance);
    }
}
