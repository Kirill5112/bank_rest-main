package com.example.bankcards.controller;

import com.example.bankcards.dto.BankCardCreateDto;
import com.example.bankcards.dto.BankCardResponseDto;
import com.example.bankcards.dto.BankCardUpdateDto;
import com.example.bankcards.service.BankCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping(value = "/api/cards")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class BankCardController {
    private final BankCardService service;

    @GetMapping("/{id}")
    public BankCardResponseDto getBankCardById(@PathVariable Long id) {
        return service.getBankCardById(id);
    }

    @Operation(summary = "Get all cards paginated")
    @GetMapping
    public Page<BankCardResponseDto> getCards(Pageable pageable) {
        return service.getCards(pageable);
    }

    @PostMapping
    public ResponseEntity<BankCardResponseDto> createBankCard(@RequestBody @Valid BankCardCreateDto dto) {
        BankCardResponseDto created = service.createBankCard(dto);
        return ResponseEntity.created(URI.create("api/cards/" + created.getId()))
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BankCardResponseDto> updateBankCard(@PathVariable Long id, @Valid @RequestBody BankCardUpdateDto dto) {
        BankCardResponseDto updated = service.updateBankCard(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBankCard(@PathVariable Long id) {
        service.deleteBankCard(id);
        return ResponseEntity.noContent().build();
    }
}
