package com.bank.account.controller;

import com.bank.account.dto.*;
import com.bank.account.exception.AccountNotFoundException;
import com.bank.account.exception.InsufficientFundsException;
import com.bank.account.model.Account;
import com.bank.account.model.Transaction;
import com.bank.user.model.User;
import com.bank.user.security.SecurityUtils;
import com.bank.account.service.AccountService;
import com.bank.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final SecurityUtils secUtils;

    private final UserService userService;

    @GetMapping
    public String getAccounts(Authentication auth, Model model) {
        String userId = secUtils.getCurrentUserId();

        List<Account> accounts = accountService.getAllAccounts(userId);
        List<Transaction> top10Transactions = accountService.getLastTransaction(userId);

        BigDecimal amount = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("accounts", accounts);
        model.addAttribute("transactions", top10Transactions);
        model.addAttribute("username", auth.getName());
        model.addAttribute("amount", amount);

        return "account";
    }

    @GetMapping("/open-account")
    public String createAccountForm(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("request", new OpenAccount());
        return "open-account";
    }

    @PostMapping("/open-account")
    public String createAccount(@Valid @ModelAttribute("request") OpenAccount request,
                                Authentication auth) {

        BigDecimal amount = BigDecimal.ZERO;
        User user = userService.findUserByIdentifier(auth.getName());

        switch (request.getType())
        {
            case "Checking":
                accountService.createCheckingAccount(user, amount);
                break;
            case "Savings":
                accountService.createSavingsAccount(user, amount);
                break;
            case "Credit":
                accountService.createCreditAccount(user, amount, new BigDecimal("50000"));
                break;
        }

        return "redirect:/account";
    }

    @GetMapping("/{accountNumber}/deposit")
    public String depositForm(@PathVariable("accountNumber") String accountNumber,
                              Authentication auth,
                              Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("accountNumber", accountNumber);
        model.addAttribute("request", new TransactionRequest());
        return "deposit";
    }

    @PostMapping("/{accountNumber}/deposit")
    public String deposit(@PathVariable("accountNumber") String accountNumber,
                          @Valid @ModelAttribute("request") TransactionRequest request) {
        accountService.deposit(accountNumber, request.getAmount());
        return "redirect:/account";
    }

    @GetMapping("/{accountNumber}/transfer")
    public String transferForm(@PathVariable String accountNumber,
                               Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("accountNumber", accountNumber);

        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber(accountNumber);
        model.addAttribute("request", request);

        return "transfer";
    }

    @PostMapping("/{accountNumber}/transfer")
    public String transfer(@PathVariable("accountNumber") String accountNumber,
                           @Valid @ModelAttribute("request") TransferRequest request,
                           BindingResult bindingResult,
                           Authentication auth, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("username", auth.getName());
            model.addAttribute("accountNumber", accountNumber);
            return "transfer";
        }

        request.setFromAccountNumber(accountNumber);

        try {
            accountService.transfer(accountNumber, request.getToAccountNumber(), request.getAmount());
            return "redirect:/account";
        } catch (AccountNotFoundException e) {
            model.addAttribute("username", auth.getName());
            model.addAttribute("accountNumber", accountNumber);
            model.addAttribute("error", "Счет получателя не найден: " + request.getToAccountNumber());
            return "transfer";
        } catch (InsufficientFundsException e) {
            model.addAttribute("username", auth.getName());
            model.addAttribute("accountNumber", accountNumber);
            model.addAttribute("error", "Недостаточно средств на счете");
            return "transfer";
        }
    }

    @GetMapping("/{accountNumber}/transaction")
    public String getAccountTransactions(@PathVariable("accountNumber") String accountNumber,
                                         Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());

        List<Transaction> transactions = accountService.getAccountTransaction(accountNumber);

        model.addAttribute("transactions", transactions);

        return "transaction";
    }
}
