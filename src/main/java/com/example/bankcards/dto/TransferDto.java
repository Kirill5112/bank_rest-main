package com.example.bankcards.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferDto {
    @NotNull
    @Min(value = 1L, message = "id > 0")
    private Long payerId;
    @NotNull
    @Min(value = 1L, message = "id > 0")
    private Long payeeId;
    @NotNull(message = "Enter sum of transfer")
    @Min(value = 10L, message = "minimum sum is 10")
    private BigDecimal sum;
}
