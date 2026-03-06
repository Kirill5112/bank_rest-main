package com.example.bankcards.dto;

import com.example.bankcards.enums.CardStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.YearMonth;

@Setter
@Getter
public class BankCardCreateDto {
    @Schema(type = "string", example = "2026-12", description = "YYYY-MM")
    @JsonFormat(pattern = "yyyy-MM")
    @NotNull
    private YearMonth expire;
    private CardStatus status;
    @Schema(type = "string", description = "phone number",
            examples = {"8 444 555 12 54", "+71234561234", "7 (783) 123-45-67"})
    @NotNull
    private String owner;
}
