package com.example.bankcards.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserResponseDto {
    private String username;  // phone
    private String firstName;
    private String lastName;
    private String middleName;
    private boolean enabled;
}
