package com.example.bankcards.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferDto {
    private Long payerId;
    private Long payeeId;
    private BigDecimal sum;
}
