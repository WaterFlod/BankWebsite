package com.bank.account.service;

import com.bank.account.model.Account;
import com.bank.account.model.Transaction;
import com.bank.account.model.TransactionType;
import com.bank.account.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public List<Transaction> getLastTransaction(String userId) {
        return transactionRepository.findTop10ByUserId(userId, Pageable.ofSize(10));
    }

    public List<Transaction> getAccountTransaction(String accountNumber) {
        return transactionRepository.findByAccountNumber(accountNumber);
    }

    public void createTransaction(Account account, BigDecimal amount, TransactionType type,
                                  String description, BigDecimal balanceAfter) {
        Transaction transaction = Transaction.builder()
                .account(account)
                .amount(amount)
                .type(type)
                .description(description)
                .balanceAfter(balanceAfter)
                .build();
        transactionRepository.save(transaction);
    }
}
