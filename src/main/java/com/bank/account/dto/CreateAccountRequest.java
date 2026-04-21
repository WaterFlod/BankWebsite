package com.bank.account.dto;

import com.bank.account.model.AccountType;
import java.math.BigDecimal;

public record CreateAccountRequest (
    String identifier,
    AccountType type,
    BigDecimal initialDeposit
){}
