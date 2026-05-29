package com.bank.account.model;

import com.bank.user.model.User;
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

    private BigDecimal principalDebit;

    private BigDecimal accruedInterest;

    private LocalDate lastInterestDate;

    public CreditAccount(String accountNumber, BigDecimal creditLimit, User user) {
        super(accountNumber, BigDecimal.ZERO, user);
        this.principalDebit = BigDecimal.ZERO;
        this.accruedInterest = BigDecimal.ZERO;
        this.lastInterestDate = LocalDate.now();
    }
}
