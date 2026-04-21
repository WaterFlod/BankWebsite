package com.bank.account.dto;

public record RegistrationRequest (
    String email,
    String phoneNumber,
    String firstName,
    String lastName
){}

