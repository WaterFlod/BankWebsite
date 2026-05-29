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
@DiscriminatorValue("SAVINGS")
@Getter
@Setter
@NoArgsConstructor
public class SavingsAccount extends Account {

    private LocalDate lastInterestDate;

    public SavingsAccount(String accountNumber, BigDecimal initialBalance, User user) {
        super(accountNumber, initialBalance, user);
        this.lastInterestDate = LocalDate.now();
    }
}
