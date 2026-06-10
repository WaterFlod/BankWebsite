package com.bank.account.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class RegistrationRequest {

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String password;

    private BigDecimal initialDeposit;
}
