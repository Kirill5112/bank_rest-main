package com.example.bankcards.service;

import com.example.bankcards.entity.BankCard;
import com.example.bankcards.exception.BlockingNotOwnCardException;
import com.example.bankcards.repository.BankCardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private BankCardRepository cardsRepo;

    @Mock
    private Authentication principal;

    @InjectMocks
    private UserService userService;


    @Test
    void blockCurrentCardShouldThrowBlockingNotOwnCard() {
        when(principal.isAuthenticated()).thenReturn(true);
        when(principal.getName()).thenReturn("owner");
        when(cardsRepo.findById(anyLong())).thenReturn(Optional.of(BankCard.builder()
                .owner("not_owner").build()));

        assertThrows(BlockingNotOwnCardException.class,() ->
                userService.blockCurrentCard(principal, 1L));
    }
}
