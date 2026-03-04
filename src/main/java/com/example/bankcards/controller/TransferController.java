package com.example.bankcards.controller;

import com.example.bankcards.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
public class TransferController {
    private final TransferService transferService;

    @PostMapping("/{payerId}/{payeeId}/{sum}")
    public boolean transfer(@PathVariable Long payerId, @PathVariable Long payeeId, @PathVariable BigDecimal sum) {
        return transferService.transfer(payerId, payeeId, sum);
    }
}
