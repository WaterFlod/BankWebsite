package com.bank.account.dto;

import com.bank.account.model.AccountType;
import lombok.Data;

import java.math.BigDecimal;

public record AccountResponse (
    String accountNumber,
    BigDecimal balance,
    AccountType type
){}
