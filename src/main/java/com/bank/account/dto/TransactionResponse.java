package com.bank.account.dto;

import com.bank.account.model.Transaction.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionDTO {
    private String accountNumber;
    private BigDecimal amount;
    private TransactionType type;
    private String description;
    private LocalDateTime timestamp;
    private BigDecimal balanceAfter;
}
