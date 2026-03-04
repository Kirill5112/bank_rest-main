package com.example.bankcards.service;

import com.example.bankcards.entity.BankCard;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.BankCardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final BankCardRepository cardsRepo;

    @Transactional
    public boolean transfer(Long payerCardId, Long payeeCardId, BigDecimal sum) {
        BankCard payer = cardsRepo.findById(payerCardId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "BankCard", payerCardId.toString()));
        BankCard payee = cardsRepo.findById(payeeCardId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "BankCard", payeeCardId.toString()));
        if (!payer.getOwnerId().equals(payee.getOwnerId()))
            throw new IllegalStateException("Transfers only between one user card");
        payer.setBalance(payer.getBalance().subtract(sum));
        payee.setBalance(payee.getBalance().add(sum));
        cardsRepo.save(payer);
        cardsRepo.save(payee);
        return true;
    }
}
