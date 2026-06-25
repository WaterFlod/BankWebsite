package com.bank.account.model;

import com.bank.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("CREDIT")
@Getter @Setter
@NoArgsConstructor
public class CreditAccount extends Account {

    @Column
    private BigDecimal principalDebit;

    @Column(nullable = false)
    private BigDecimal creditLimit;

    @Column
    private BigDecimal accruedInterest;

    @Column
    private LocalDate lastInterestDate;

    public CreditAccount(String accountNumber, BigDecimal initialBalance, BigDecimal creditLimit, User user) {
        super(accountNumber, creditLimit.add(initialBalance), user);
        this.creditLimit = creditLimit;
        this.principalDebit = initialBalance;
        this.accruedInterest = BigDecimal.ZERO;
        this.lastInterestDate = LocalDate.now();
    }
}
