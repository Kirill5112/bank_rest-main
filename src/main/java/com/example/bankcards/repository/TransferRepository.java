package com.example.bankcards.repository;

import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.Transfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    Page<Transfer> findByPayeeInOrPayerIn(
            Collection<BankCard> payees, Collection<BankCard> payers, Pageable pageable);
}
