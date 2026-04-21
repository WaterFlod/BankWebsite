package com.bank.account.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends BaseException {
    public AuthenticationException(String message) {

        super("401", HttpStatus.UNAUTHORIZED ,message);
    }
}
