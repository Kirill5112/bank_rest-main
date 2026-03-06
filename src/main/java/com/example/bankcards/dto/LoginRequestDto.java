package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {
    @Schema(type = "string", description = "phone number",
            examples = {"8 444 555 12 54", "+71234561234", "7 (783) 123-45-67"})
    @Size(min = 10, message = "phone number is incorrect")
    private String username;
    private String password;
}