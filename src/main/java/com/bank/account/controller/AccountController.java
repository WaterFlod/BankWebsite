package com.bank.account.controller;

import com.bank.account.dto.*;
import com.bank.account.security.SecurityUtils;
import com.bank.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final SecurityUtils secUtils;

    @GetMapping
    public String getAccounts(Model model) {
        List<AccountResponse> accounts = accountService.getAllAccounts(secUtils.getCurrentUserId());
        model.addAttribute("accounts", accounts);
        return "account";
    }

    @PostMapping("/open-account")
    public String createAccount() {
        return "redirect:/open-account";
    }
}
