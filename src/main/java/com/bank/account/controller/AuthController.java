package com.bank.account.controller;

import com.bank.account.dto.RegistrationRequest;
import com.bank.account.dto.UserDTO;
import com.bank.account.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Validated @RequestBody RegistrationRequest request) {
        UserDTO user = authService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}
