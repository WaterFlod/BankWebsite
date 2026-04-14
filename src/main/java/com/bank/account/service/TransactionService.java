package com.bank.account.service;

import com.bank.account.model.Account;
import com.bank.account.model.Transaction;
import com.bank.account.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private Transaction createTransaction(Account account, BigDecimal amount,
                                          Transaction.TransactionType type, String description) {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setDescription(description);
        transaction.setBalanceAfter(account.getBalance());

        return transactionRepository.save(transaction);
    }
}
