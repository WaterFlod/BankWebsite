package com.bank.account.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SetPassword {
        private String password;
        private String confirmPassword;
}
