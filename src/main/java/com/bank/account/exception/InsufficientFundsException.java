package com.bank.account.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

@Getter
public class InsufficientFundsException extends BaseException {
    public InsufficientFundsException(String message) {
        super("INSUFFICIENT_FUNDS", HttpStatus.BAD_REQUEST, message);
    }
}
