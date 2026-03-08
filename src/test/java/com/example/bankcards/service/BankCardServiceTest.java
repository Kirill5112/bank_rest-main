package com.example.bankcards.service;

import com.example.bankcards.dto.BankCardCreateDto;
import com.example.bankcards.dto.BankCardResponseDto;
import com.example.bankcards.dto.BankCardUpdateDto;
import com.example.bankcards.entity.BankCard;
import com.example.bankcards.exception.ReducingExpireException;
import com.example.bankcards.repository.BankCardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

import static com.example.bankcards.util.PhoneNormalizer.normalizePhone;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BankCardServiceTest {
    @Mock
    private BankCardRepository repo;
    @Mock
    private ModelMapper mapper;

    @InjectMocks
    BankCardService bankCardService;

    private final String correctPhone = "+7(999)123-45-67";
    private final String normalizedPhone = normalizePhone(correctPhone);
    private BankCard mappedCard;

    @BeforeEach
    void setUp() {
        mappedCard = new BankCard();
    }

    @Test
    void createBankCardShouldNormalizePhone() {
        BankCardCreateDto createDto = new BankCardCreateDto();
        createDto.setOwner(correctPhone);

        when(mapper.map(createDto, BankCard.class)).thenReturn(mappedCard);
        when(repo.save(any(BankCard.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.map(any(BankCard.class), eq(BankCardResponseDto.class))).thenReturn(new BankCardResponseDto());

        bankCardService.createBankCard(createDto);

        verify(repo).save(argThat(bankCard ->
                bankCard.getOwner().equals(normalizedPhone)));
    }

    @Test
    void updateShouldThrowReducingExpire() {
        YearMonth current = YearMonth.from(LocalDate.of(2025, 12, 20));
        YearMonth reduced = YearMonth.from(LocalDate.of(2020, 12, 20));

        BankCard currentCard = BankCard.builder().expire(current).build();
        BankCardUpdateDto updateDto = new BankCardUpdateDto();
        updateDto.setExpire(reduced);

        when(repo.findById(anyLong())).thenReturn(Optional.of(currentCard));
        doNothing().when(mapper).map(eq(updateDto), any(BankCard.class));

        assertThrows(ReducingExpireException.class, () ->
                bankCardService.updateBankCard(1L, updateDto));
    }
}
