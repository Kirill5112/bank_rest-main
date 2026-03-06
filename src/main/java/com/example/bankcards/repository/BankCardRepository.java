package com.example.bankcards.repository;

import com.example.bankcards.entity.BankCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BankCardRepository extends JpaRepository<BankCard, Long> {

    @Query("""
select card
from BankCard card where
card.owner = :owner AND (
card.numberLast4 ILIKE concat('%', :search, '%') OR
cast(card.balance as string) ILIKE concat('%', :search, '%') OR
cast(card.expire as string) ILIKE concat('%', :search, '%') OR
cast(card.status as string) ILIKE concat('%', :search, '%'))""")
    Page<BankCard> findByOwnerIdWithSearch(String owner, Pageable pageable, String search);

    Page<BankCard> findByOwner(String owner, Pageable pageable);

    List<BankCard> findByOwner(String owner);
}
