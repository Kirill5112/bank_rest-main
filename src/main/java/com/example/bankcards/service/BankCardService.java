package com.example.bankcards.service;

import com.example.bankcards.dto.BankCardCreateDto;
import com.example.bankcards.dto.BankCardResponseDto;
import com.example.bankcards.dto.BankCardUpdateDto;
import com.example.bankcards.entity.BankCard;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.BankCardRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankCardService {
    private final BankCardRepository repo;
    private final ModelMapper mapper;

    public BankCardResponseDto createBankCard(BankCardCreateDto dto) {
        BankCard card = mapper.map(dto, BankCard.class);
        return mapper.map(repo.save(card), BankCardResponseDto.class);
    }

    public BankCardResponseDto getBankCardById(Long id) {
        BankCard model = repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("BankCard", id.toString()));
        return mapper.map(model, BankCardResponseDto.class);
    }

    public Page<BankCardResponseDto> getCards(Pageable pageable) {
        return repo.findAll(pageable).map(
                bankCard -> mapper.map(
                        bankCard, BankCardResponseDto.class));
    }

    public BankCardResponseDto updateBankCard(Long id, BankCardUpdateDto dto) {
        BankCard model = repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("BankCard", id.toString()));

        mapper.map(dto, model);
        model = repo.save(model);
        return mapper.map(model, BankCardResponseDto.class);
    }

    public void deleteBankCard(Long id) {
        repo.deleteById(id);
    }


}
