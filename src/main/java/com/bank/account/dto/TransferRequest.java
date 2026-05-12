package com.bank.account.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class TransferRequest {

    private String fromAccountNumber;

    private String toAccountNumber;

    @Positive(message = "Сумма должна быть больше нуля")
    private BigDecimal amount;
}
