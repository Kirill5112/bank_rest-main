package com.example.bankcards.repository;

import com.example.bankcards.entity.BankCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankCardRepository extends JpaRepository<BankCard, Long> {
    List<BankCard> findByOwnerId(Long ownerId);
}
