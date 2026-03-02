package com.example.bankcards.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDate;
import java.time.YearMonth;

@Converter(autoApply = true)
public class YearMonthAttributeConverter implements AttributeConverter<YearMonth, LocalDate> {
    @Override
    public LocalDate convertToDatabaseColumn(YearMonth attribute) {
        return attribute != null ? attribute.atDay(1) : null;
    }

    @Override
    public YearMonth convertToEntityAttribute(LocalDate dbData) {
        return dbData != null ? YearMonth.from(dbData) : null;
    }
}

