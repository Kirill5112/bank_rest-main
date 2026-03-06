package com.example.bankcards.dto;

import com.example.bankcards.enums.CardStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.Setter;

import java.time.YearMonth;

@Setter
@Getter
public class BankCardUpdateDto {
    @Schema(type = "string", example = "2026-12", description = "YYYY-MM")
    @JsonFormat(pattern = "yyyy-MM")
    private YearMonth expire;
    private CardStatus status;

    @AssertTrue(message = "one of the fields required")
    public boolean dtoNotEmpty() {
        return expire != null || status != null;
    }
}
