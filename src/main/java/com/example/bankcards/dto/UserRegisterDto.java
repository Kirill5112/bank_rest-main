package com.example.bankcards.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRegisterDto {
    private String username;
    private String firstName;
    private String lastName;
    private String middleName;
    private String password;

}
