package com.bank.account.model;

import com.bank.user.model.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("CHECKING")
@NoArgsConstructor
public class CheckingAccount extends Account{

    public CheckingAccount(String accountNumber, BigDecimal initialBalance, User user) {
        super(accountNumber, initialBalance, user);
    }

}
