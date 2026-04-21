package com.bank.account.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class RegistrationDebitRequest {

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private BigDecimal initialDeposit;
}
