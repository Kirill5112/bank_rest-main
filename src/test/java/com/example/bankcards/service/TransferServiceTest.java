package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.exception.AuthenticationMismatchException;
import com.example.bankcards.exception.IllegalBalanceChangeException;
import com.example.bankcards.exception.IllegalTransferException;
import com.example.bankcards.exception.NegativeBalanceException;
import com.example.bankcards.repository.BankCardRepository;
import com.example.bankcards.repository.TransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransferServiceTest {
    @Mock
    private BankCardRepository cardsRepo;
    @Mock
    private TransferRepository transferRepo;

    @InjectMocks
    private TransferService transferService;

    @Mock
    private Authentication principal;

    private TransferRequestDto transferReq;
    private final String owner = "test_owner";
    private BankCard payer;
    private BankCard payee;

    @BeforeEach
    void setUp() {
        when(principal.isAuthenticated()).thenReturn(true);
        transferReq = new TransferRequestDto();
        transferReq.setPayerId(1L);
        transferReq.setPayeeId(2L);
        payer = BankCard.builder()
                .owner(owner)
                .balance(new BigDecimal("10000.00")).build();
        payee = new BankCard();
        payer.setOwner(owner);
        payee.setOwner(owner);

    }

    @Test
    void transfer_ThrowsNegativeBalance_AND_IllegalBalanceChange() {
        when(principal.getName()).thenReturn(owner);
        payer.setBalance(new BigDecimal(100));
        transferReq.setAmount(new BigDecimal(1000));

        when(cardsRepo.findById(transferReq.getPayerId())).thenReturn(Optional.of(payer));
        when(cardsRepo.findById(transferReq.getPayeeId())).thenReturn(Optional.of(payee));

        assertThrows(NegativeBalanceException.class, () ->
                transferService.transfer(principal, transferReq));
        payer.setBalance(new BigDecimal(10000));
        payer.setStatus(CardStatus.BLOCKED);
        assertThrows(IllegalBalanceChangeException.class, () ->
                transferService.transfer(principal, transferReq));
        payer.setStatus(CardStatus.EXPIRED);
        assertThrows(IllegalBalanceChangeException.class, () ->
                transferService.transfer(principal, transferReq));
    }

    @Test
    void transfer_ThrowAuthenticationMismatch_WhenNotAuthenticated() {
        when(principal.isAuthenticated()).thenReturn(false);

        assertThrows(AuthenticationMismatchException.class,() ->
                transferService.transfer(principal, transferReq));
    }

    @Test
    void transfer_ShouldThrowIllegalTransfer_WhenSameCard() {
        TransferRequestDto request = TransferRequestDto.builder()
                .payerId(1L)
                .payeeId(1L)
                .build();

        when(cardsRepo.findById(1L)).thenReturn(Optional.of(payer));

        assertThrows(IllegalTransferException.class, () -> transferService.transfer(principal, request));
        verifyNoInteractions(transferRepo);
    }

    @Test
    void getCurrentTransferById_ShouldThrowAccessDenied(){
        payer.setOwner("different");
        payee.setOwner("different");
        Transfer transfer = Transfer.builder()
                .payee(payee)
                        .payer(payer).build();

        when(principal.getName()).thenReturn("current");
        when(transferRepo.findById(1L)).thenReturn(Optional.of(transfer));
        assertThrows(AccessDeniedException.class, () ->
                transferService.getCurrentTransferById(1L, principal));
    }
}
