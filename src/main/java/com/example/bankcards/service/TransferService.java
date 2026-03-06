package com.example.bankcards.service;

import com.example.bankcards.entity.BankCard;
import com.example.bankcards.exception.IllegalTransferException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.BankCardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;

import static com.example.bankcards.util.SessionUserHelper.getSessionUsername;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final BankCardRepository cardsRepo;

    @Transactional
    public boolean transfer(Principal principal,
                            Long payerCardId, Long payeeCardId, BigDecimal sum) {

        if(payeeCardId.equals(payerCardId))
            return true;
        String username = getSessionUsername(principal);
        BankCard payer = cardsRepo.findById(payerCardId).orElseThrow(() ->
                new ResourceNotFoundException("BankCard", payerCardId.toString()));
        BankCard payee = cardsRepo.findById(payeeCardId).orElseThrow(() ->
                new ResourceNotFoundException("BankCard", payeeCardId.toString()));
        boolean isCurrenUserCards = payer.getOwner().equals(username)
                                    && payee.getOwner().equals(username);
        if (!isCurrenUserCards)
            throw new IllegalTransferException();
        payer.setBalance(payer.getBalance().subtract(sum));
        payee.setBalance(payee.getBalance().add(sum));
        cardsRepo.save(payer);
        cardsRepo.save(payee);
        return true;
    }
}
