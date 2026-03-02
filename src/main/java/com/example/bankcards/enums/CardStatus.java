package com.example.bankcards.enums;

import lombok.Getter;

public enum CardStatus {
    ACTIVE("Активна"),
    BLOCKED("Заблокирована"),
    EXPIRED("Истек срок");

    @Getter
    private final String description;

    CardStatus(String description) {
        this.description = description;
    }
}
