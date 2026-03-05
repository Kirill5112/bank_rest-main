package com.example.bankcards.service;

import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.IllegalTransferException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.BankCardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;

import static com.example.bankcards.util.SessionUserHelper.getSessionUser;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final BankCardRepository cardsRepo;

    @Transactional
    public boolean transfer(Principal principal,
                            Long payerCardId, Long payeeCardId, BigDecimal sum) {

        User user = getSessionUser(principal);
        BankCard payer = cardsRepo.findById(payerCardId).orElseThrow(() ->
                new ResourceNotFoundException("BankCard", payerCardId.toString()));
        BankCard payee = cardsRepo.findById(payeeCardId).orElseThrow(() ->
                new ResourceNotFoundException("BankCard", payeeCardId.toString()));
        Long userId = user.getId();
        if (!payer.getOwnerId().equals(userId) || !payee.getOwnerId().equals(userId))
            throw new IllegalTransferException();
        payer.setBalance(payer.getBalance().subtract(sum));
        payee.setBalance(payee.getBalance().add(sum));
        cardsRepo.save(payer);
        cardsRepo.save(payee);
        return true;
    }
}
