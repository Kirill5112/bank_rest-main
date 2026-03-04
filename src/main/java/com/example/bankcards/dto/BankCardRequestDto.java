package com.example.bankcards.dto;

import com.example.bankcards.enums.CardStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.YearMonth;

@Setter
@Getter
@NoArgsConstructor
public class BankCardRequestDto {
    @Schema(type = "string", example = "2026-12", description = "YYYY-MM")
    @JsonFormat(pattern = "yyyy-MM")
    private YearMonth expire;
    private CardStatus status;
    private Long ownerId;
}
