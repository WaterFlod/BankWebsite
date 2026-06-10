package com.bank.account.dto;

import com.bank.account.model.TransactionType;
import java.math.BigDecimal;

public record TransactionResponse (
    String accountNumber,
    String description,
    BigDecimal amount,
    TransactionType type,
    String timestamp,
    BigDecimal balanceAfter
){}
