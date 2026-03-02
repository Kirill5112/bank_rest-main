package com.example.bankcards.entity;

import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.exception.IllegalBalanceChange;
import com.example.bankcards.exception.IllegalExpireChange;
import com.example.bankcards.util.CardNumberData;
import com.example.bankcards.util.CardNumberGenerator;
import com.example.bankcards.util.YearMonthAttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.YearMonth;

import static com.example.bankcards.enums.CardStatus.BLOCKED;
import static com.example.bankcards.enums.CardStatus.EXPIRED;

@Entity
@Table(name = "bank_cards")
@Getter
public class BankCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Convert(converter = YearMonthAttributeConverter.class)
    @Column(name = "expire", nullable = false)
    private YearMonth expire;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Setter
    private CardStatus status;

    @Column(name = "balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(name = "number_last4", nullable = false, length = 4)
    private String numberLast4;

    @Column(name = "number_mask", nullable = false, length = 19)
    private String maskedNumber;

    @Column(name = "number_hash", nullable = false, unique = true)
    private byte[] numberHash;

    public void changeBalance(BigDecimal difference) {
        //Срок истёк или попытка списания с заблокированной карты
        if (status == EXPIRED || (status == BLOCKED && difference.compareTo(BigDecimal.ZERO) < 0))
            throw new IllegalBalanceChange(status, id);
        BigDecimal changedBalance = balance.add(difference);
        if (changedBalance.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalBalanceChange(id);
        balance = changedBalance;
    }

    public void setExpire(YearMonth newExpire) {
        if (expire != null && newExpire.isBefore(expire))
            throw new IllegalExpireChange(id);
        expire = newExpire;
    }

    @PrePersist
    public void init() {
        CardNumberData numberData = CardNumberGenerator.generate();
        numberLast4 = numberData.numberLast4();
        numberHash = numberData.numberHash();
        maskedNumber = numberData.maskedNumber();
        balance = new BigDecimal("0.00");
    }
}
