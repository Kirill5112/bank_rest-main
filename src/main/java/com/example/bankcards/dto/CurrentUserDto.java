package com.example.bankcards.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class CurrentUserDto {
    private String username;
    private String firstName;
    private String lastName;
    private String middleName;
    private BigDecimal fullBalance;
}
