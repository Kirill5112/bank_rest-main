package com.example.bankcards.dto;

import com.example.bankcards.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
public class LoginRequestDto {
    @Schema(type = "string", description = "phone number",
            examples = {"8 444 555 12 54", "+71234561234", "7 (783) 123-45-67"})
    @Size(min = 10, message = "phone number is incorrect")
    private String username;

    public void setUsername(String username) {
        User u = new User();
        u.setUsername(username);
        this.username = u.getUsername();
    }

    @Setter
    private String password;
}