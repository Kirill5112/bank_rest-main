package com.example.bankcards.controller;

import com.example.bankcards.annotation.AdminOnly;
import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.dto.TransferResponseDto;
import com.example.bankcards.service.TransferService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.security.Principal;

@RestController
@RequestMapping("/api/transfers")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class TransferController {
    private final TransferService transferService;

    @AdminOnly
    @GetMapping
    public Page<TransferResponseDto> getTransfers(Pageable pageable){
        return transferService.getAll(pageable);
    }

    @AdminOnly
    @GetMapping("/{id}")
    public TransferResponseDto getTransferById (@PathVariable Long id){
        return transferService.getTransferById(id);
    }

    @GetMapping("/current")
    public Page<TransferResponseDto> getCurrentTransfers (Pageable p, Principal pr){
        return transferService.getCurrentTransfers(p, pr);
    }

    @GetMapping("/current/{id}")
    public TransferResponseDto getCurrentTransferById (@PathVariable Long id, Principal pr){
        return transferService.getCurrentTransferById(id, pr);
    }
    @PostMapping
    public ResponseEntity<TransferResponseDto> transfer(
            Principal pr, @RequestBody @Valid TransferRequestDto dto) {
        TransferResponseDto transfer = transferService.transfer(pr, dto);
        return ResponseEntity.created(URI.create("/api/transfers/current/" + transfer.getId())).body(transfer);
    }
}
