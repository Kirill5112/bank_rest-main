package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.dto.TransferResponseDto;
import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.exception.IllegalTransferException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.BankCardRepository;
import com.example.bankcards.repository.TransferRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;

import static com.example.bankcards.util.SessionUserHelper.getSessionUsername;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final BankCardRepository cardsRepo;
    private final TransferRepository transferRepo;
    private final ModelMapper mapper;


    public Page<TransferResponseDto> getAll(Pageable pageable) {
        return transferRepo.findAll(pageable).map(t ->
                TransferResponseDto.builder()
                        .id(t.getId())
                        .amount(t.getAmount())
                        .payerMask(t.getPayer().getMaskedNumber())
                        .payeeMask(t.getPayee().getMaskedNumber())
                        .created(t.getCreated()).build());
    }

    public TransferResponseDto getTransferById(Long id) {
        Transfer transfer = transferRepo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Transfer", id.toString()));
        return TransferResponseDto.builder()
                .id(transfer.getId())
                .amount(transfer.getAmount())
                .payerMask(transfer.getPayer().getMaskedNumber())
                .payeeMask(transfer.getPayee().getMaskedNumber())
                .created(transfer.getCreated()).build();
    }

    public Page<TransferResponseDto> getCurrentTransfers(Pageable p, Principal pr) {
        String username = getSessionUsername(pr);
        var cards = cardsRepo.findByOwner(username);
        var transfers = transferRepo.findByPayeeInOrPayerIn(cards, cards, p);
        return transfers.map(t ->
                TransferResponseDto.builder()
                        .id(t.getId())
                        .amount(t.getAmount())
                        .payerMask(t.getPayer().getMaskedNumber())
                        .payeeMask(t.getPayee().getMaskedNumber())
                        .created(t.getCreated()).build());
    }

    public TransferResponseDto getCurrentTransferById(Long id, Principal pr) {
        String username = getSessionUsername(pr);
        Transfer transfer = transferRepo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Transfer", id.toString()));
        String payerOwner = transfer.getPayer().getOwner();
        String payeeOwner = transfer.getPayee().getOwner();
        if (!payerOwner.equals(username) && !payeeOwner.equals(username))
            throw new AccessDeniedException();
        return TransferResponseDto.builder()
                .id(transfer.getId())
                .amount(transfer.getAmount())
                .payerMask(transfer.getPayer().getMaskedNumber())
                .payeeMask(transfer.getPayee().getMaskedNumber())
                .created(transfer.getCreated()).build();
    }

    @Transactional
    public TransferResponseDto transfer(Principal principal, TransferRequestDto request) {
        Long payerCardId = request.getPayerId(), payeeCardId = request.getPayeeId();
        BigDecimal amount = request.getAmount();
        String username = getSessionUsername(principal);

        BankCard payer = cardsRepo.findById(payerCardId).orElseThrow(() ->
                new ResourceNotFoundException("BankCard", payerCardId.toString()));
        BankCard payee = cardsRepo.findById(payeeCardId).orElseThrow(() ->
                new ResourceNotFoundException("BankCard", payeeCardId.toString()));

        boolean isCurrenUserCards = payer.getOwner().equals(username)
                                    && payee.getOwner().equals(username);
        if (!isCurrenUserCards || payeeCardId.equals(payerCardId))
            throw new IllegalTransferException();

        payer.setBalance(payer.getBalance().subtract(amount));
        payee.setBalance(payee.getBalance().add(amount));
        cardsRepo.save(payer);
        cardsRepo.save(payee);

        Transfer transfer = Transfer.builder()
                .amount(amount)
                .payer(payer)
                .payee(payee).build();
        transfer = transferRepo.save(transfer);
        TransferResponseDto response = mapper.map(transfer, TransferResponseDto.class);
        response.setPayerMask(payer.getMaskedNumber());
        response.setPayeeMask(payee.getMaskedNumber());
        return response;
    }

}
