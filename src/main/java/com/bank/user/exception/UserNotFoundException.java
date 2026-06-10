package com.bank.user.exception;

import com.bank.account.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException(String email) {
        super("USER_NOT_FOUND", HttpStatus.NOT_FOUND,
                "Пользователь с таким " + email + " не найден");
    }

    public UserNotFoundException(String email, Throwable cause) {
        super("USER_NOT_FOUND", HttpStatus.NOT_FOUND,
                "Пользователь с таким " + email + " не найден", cause);
    }
}
