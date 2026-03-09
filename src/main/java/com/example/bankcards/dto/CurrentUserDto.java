package com.example.bankcards.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrentUserDto {
    private String username;
    private String firstName;
    private String lastName;
    private String middleName;
    private BigDecimal fullBalance;
}
