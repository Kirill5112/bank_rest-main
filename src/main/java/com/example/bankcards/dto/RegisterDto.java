package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterDto {
    @Schema(type = "string", description = "phone number",
            examples = {"8 444 555 12 54", "+71234561234", "7 (783) 123-45-67"})
    @Size(min = 10, message = "phone number incorrect")
    private String username;

    @NotBlank(message = "first name is required")
    private String firstName;

    @NotBlank(message = "last name is required")
    private String lastName;

    private String middleName;

    @NotBlank(message = "password is required")
    @Size(min = 5, message = "password should be at least 5 chars")
    private String password;

}
