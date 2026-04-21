package com.bank.account.service;

import com.bank.account.dto.*;
import com.bank.account.exception.AccountNotFoundException;
import com.bank.account.exception.InsufficientFundsException;
import com.bank.account.model.Account;
import com.bank.account.model.User;
import com.bank.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionService transactionService;
    private final UserService userService;

    @Transactional
    public TransactionResponse createAccount(CreateAccountRequest request) {
        String accountNumber = generateAccountNumber();

        User user = userService.findUserByIdentifier(request.identifier());

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .type(request.type())
                .user(user)
                .balance(request.initialDeposit())
                .build();

        account = accountRepository.save(account);

        TransactionResponse response = transactionService.deposit(
                account,
                request.initialDeposit(),
                "Первоначальный депозит"
        );

        log.info("Счет создан: {}", accountNumber);
        return response;
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccount(String accountNumber) {
        Account account = findAccountByNumber(accountNumber);
        return convertToAccountResponse(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts(String userId) {
        return accountRepository.findByUserId(userId)
                .stream()
                .map(this::convertToAccountResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TransactionResponse deposit(String accountNumber, TransactionRequest request) {
        Account account = findAccountByNumber(accountNumber);

        BigDecimal newBalance = account.getBalance().add(request.getAmount());
        account.setBalance(newBalance);
        accountRepository.save(account);

        TransactionResponse response = transactionService.deposit(
                account,
                request.getAmount(),
                "Пополнение счета " + account.getAccountNumber() + " на сумму " + request.getAmount()
        );

        return response;
    }

    @Transactional
    public TransactionResponse withdraw(String accountNumber, TransactionRequest request) {
        Account account = findAccountByNumber(accountNumber);

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException(accountNumber, account.getBalance(),request.getAmount());
        }

        BigDecimal newBalance = account.getBalance().subtract(request.getAmount());
        account.setBalance(newBalance);

        accountRepository.save(account);

        TransactionResponse response = transactionService.withdraw(
                account,
                request.getAmount(),
                "Убыль счета " + account.getAccountNumber() + " на сумму " + request.getAmount()
        );

        return response;
    }

    @Transactional
    public TransferResponse transfer(TransferRequest request) {
        Account fromAccount = findAccountByNumber(request.getFromAccountNumber());
        Account toAccount = findAccountByNumber(request.getToAccountNumber());

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException(fromAccount.getAccountNumber(), fromAccount.getBalance(), request.getAmount());
        }

        // Снимаем со счёта отправителя
        BigDecimal fromNewBalance = fromAccount.getBalance().subtract(request.getAmount());
        fromAccount.setBalance(fromNewBalance);
        accountRepository.save(fromAccount);

        TransferResponse response = transactionService.transferFrom(
                fromAccount,
                toAccount.getAccountNumber(),
                request.getAmount()
        );

        // Добавляем на счёт получателя
        BigDecimal toNewBalance = toAccount.getBalance().add(request.getAmount());
        toAccount.setBalance(toNewBalance);
        accountRepository.save(toAccount);

        transactionService.transferTo(toAccount, fromAccount.getAccountNumber(), request.getAmount());

        log.info("Перевод выполнен: {} -> {}, сумма: {}",
                fromAccount.getAccountNumber(),
                toAccount.getAccountNumber(),
                request.getAmount()
        );

        return response;
    }

    private Account findAccountByNumber(String accountNumber) {
            return accountRepository.findByAccountNumber(accountNumber)
                    .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    private String generateAccountNumber() {
        return "ACC" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }

    private AccountResponse convertToAccountResponse(Account account) {
        return new AccountResponse(
                account.getAccountNumber(),
                account.getBalance(),
                account.getType()
        );
    }
}
