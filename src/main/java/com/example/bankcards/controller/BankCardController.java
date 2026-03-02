package com.example.bankcards.controller;

import com.example.bankcards.dto.BankCardRequestDto;
import com.example.bankcards.dto.BankCardResponseDto;
import com.example.bankcards.service.BankCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/cards")
@RequiredArgsConstructor
public class BankCardController {
    private final BankCardService service;

    @GetMapping("/{id}")
    public BankCardResponseDto getBankCardById(@PathVariable Long id) {
        return service.getBankCardById(id);
    }

    @GetMapping("/all")
    public List<BankCardResponseDto> getAllCards() {
        return service.getAll();
    }

    @PostMapping
    public BankCardResponseDto createBankCard(@RequestBody BankCardRequestDto dto) {
        return service.createBankCard(dto);
    }

    @PutMapping("/{id}")
    public BankCardResponseDto updateBankCard(@PathVariable Long id, @RequestBody BankCardRequestDto dto) {
        return service.updateBankCard(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBankCard(@PathVariable Long id) {
        service.deleteBankCard(id);
    }
}
