package com.bank.account.service;

import com.bank.account.dto.*;
import com.bank.account.model.*;
import com.bank.account.repository.CreditAccountRepository;
import com.bank.account.repository.SavingsAccountRepository;
import com.bank.account.exception.AccountNotFoundException;
import com.bank.account.exception.InsufficientFundsException;
import com.bank.account.repository.TransactionRepository;
import com.bank.user.model.User;
import com.bank.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final SavingsAccountRepository savingsAccountRepository;
    private final CreditAccountRepository creditAccountRepository;
    private final TransactionRepository transactionRepository;

    private static final BigDecimal SAVINGS_RATE = new BigDecimal("0.05");
    private static final BigDecimal CREDIT_RATE = new BigDecimal("0.20");
    private static final BigDecimal DAYS_IN_YEAR = new BigDecimal("365");
    private static final MathContext MC = new MathContext(10, RoundingMode.HALF_UP);

    @Transactional
    public CheckingAccount createCheckingAccount(User user, BigDecimal initialBalance) {
        CheckingAccount account = new CheckingAccount(generateAccountNumber(), initialBalance, user);
        account = accountRepository.save(account);

        if (initialBalance.compareTo(BigDecimal.ZERO) > 0) {
            createTransaction(account, initialBalance, TransactionType.DEPOSIT,
                    "Начальный депозит", account.getBalance());
        }
        log.info("Создан расчетный счет {} для пользователя {}", account.getAccountNumber(), user.getEmail());
        return account;
    }

    @Transactional
    public SavingsAccount createSavingsAccount(User user, BigDecimal initialBalance) {
        SavingsAccount account = new SavingsAccount(generateAccountNumber(), initialBalance, user);
        account = accountRepository.save(account);

        createTransaction(account, initialBalance, TransactionType.DEPOSIT,
                "Начальный депозит", account.getBalance());
        log.info("Создан накопительный счет {} для пользователя {}", account.getAccountNumber(), user.getEmail());
        return account;
    }

    @Transactional
    public CreditAccount createCreditAccount(User user, BigDecimal initialBalance, BigDecimal creditLimit) {
        CreditAccount account = new CreditAccount(generateAccountNumber(), initialBalance, creditLimit, user);
        account = accountRepository.save(account);
        log.info("Создан кредитный счет {} с лимитом {} для пользователя {}",
                account.getAccountNumber(), creditLimit, user.getEmail());
        return account;
    }

    @Transactional(readOnly = true)
    public List<Account> getAllAccounts(String userId) {
        return accountRepository.findByUserId(userId);
    }

    @Transactional
    public void deposit(String accountNumber, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма пополнения должна быть положительной");
        }

        Account account = getAccount(accountNumber);
        BigDecimal balanceBefore = account.getBalance();
        BigDecimal newBalance = balanceBefore.add(amount);

        if (account instanceof CreditAccount credit) {
            credit.setPrincipalDebit(credit.getPrincipalDebit().subtract(amount));
            if (credit.getPrincipalDebit().compareTo(BigDecimal.ZERO) < 0) {
                credit.setPrincipalDebit(BigDecimal.ZERO);
            }
        }

        account.setBalance(newBalance);
        accountRepository.save(account);

        createTransaction(account, amount, TransactionType.DEPOSIT,
                "Пополнение счета " + accountNumber + " на сумму " + amount, newBalance);
        log.info("Пополнение счета {} на {}: новый баланс {}", accountNumber, amount, newBalance);

    }

    @Transactional
    public void withdraw(String accountNumber, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма снятия должна быть положительной");
        }

        Account account = getAccount(accountNumber);
        validateSufficientFunds(account, amount);

        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);

        if (account instanceof CreditAccount credit) {
            credit.setPrincipalDebit(credit.getPrincipalDebit().add(amount));
        }
        accountRepository.save(account);

        createTransaction(account, amount, TransactionType.WITHDRAWAL,
                "Снятие со счета " + account.getAccountNumber() + " на сумму " + amount, newBalance);
        log.info("Снятие со счёта {} суммы {}: новый баланс {}", accountNumber, amount, newBalance);
    }

    @Transactional
    public void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new IllegalArgumentException("Нельзя перевести на тот же счет");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма перевода должна быть положительной");
        }

        Account from = getAccount(fromAccountNumber);
        Account to = getAccount(toAccountNumber);

        validateSufficientFunds(from, amount);

        BigDecimal fromNewBalance = from.getBalance().subtract(amount);
        from.setBalance(fromNewBalance);
        if (from instanceof CreditAccount creditFrom) {
            creditFrom.setPrincipalDebit(creditFrom.getPrincipalDebit().add(amount));
        }

        BigDecimal toNewBalance = to.getBalance().add(amount);
        to.setBalance(toNewBalance);
        if (to instanceof CreditAccount creditTo) {
            creditTo.setPrincipalDebit(creditTo.getPrincipalDebit().subtract(amount));
            if (creditTo.getPrincipalDebit().compareTo(BigDecimal.ZERO) < 0) {
                creditTo.setPrincipalDebit(BigDecimal.ZERO);
            }
        }


        accountRepository.save(from);
        accountRepository.save(to);

        createTransaction(from, amount, TransactionType.TRANSFER_OUT,
                "Перевод на счет " + toAccountNumber, fromNewBalance);
        createTransaction(to, amount, TransactionType.TRANSFER_IN,
                "Перевод со счета " + fromAccountNumber, toNewBalance);
        log.info("Перевод {} со счета {} на счет {} выполнен",
                amount, fromAccountNumber, toAccountNumber);
    }

    @Transactional
    public void applyInterestToSavings() {
        LocalDate today = LocalDate.now();
        List<SavingsAccount> accounts = savingsAccountRepository.findByLastInterestDateBefore(today);

        for (SavingsAccount sa: accounts) {
            long days = ChronoUnit.DAYS.between(sa.getLastInterestDate(), today);
            if (days <= 0) continue;

            BigDecimal dailyRate = SAVINGS_RATE.divide(DAYS_IN_YEAR, MC);
            BigDecimal interest = sa.getBalance()
                    .multiply(dailyRate)
                    .multiply(BigDecimal.valueOf(days))
                    .setScale(2, RoundingMode.HALF_UP);

            if (interest.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal newBalance = sa.getBalance().add(interest);
                sa.setBalance(newBalance);
                sa.setLastInterestDate(today);
                savingsAccountRepository.save(sa);

                createTransaction(sa, interest, TransactionType.INTEREST,
                        "Начислены проценты за " + days + " дн.", newBalance);
                log.info("Начислены проценты {} на счет {}", interest, sa.getAccountNumber());
            }
        }
    }

    @Transactional
    public void applyInterestToCredit() {
        LocalDate today = LocalDate.now();
        List<CreditAccount> accounts = creditAccountRepository.findByLastInterestDateBefore(today);

        for (CreditAccount ca: accounts) {
            long days = ChronoUnit.DAYS.between(ca.getLastInterestDate(), today);
            if (days <= 0 || ca.getPrincipalDebit().compareTo(BigDecimal.ZERO) == 0) continue;

            BigDecimal dailyRate = CREDIT_RATE.divide(DAYS_IN_YEAR, MC);
            BigDecimal interest = ca.getPrincipalDebit()
                    .multiply(dailyRate)
                    .multiply(BigDecimal.valueOf(days))
                    .setScale(2, RoundingMode.HALF_UP);

            if (interest.compareTo(BigDecimal.ZERO) > 0) {
                ca.setAccruedInterest(ca.getAccruedInterest().add(interest));
                BigDecimal newBalance = ca.getBalance().subtract(interest);
                ca.setBalance(newBalance);
                ca.setLastInterestDate(today);
                creditAccountRepository.save(ca);

                createTransaction(ca, interest, TransactionType.INTEREST,
                        "Начислены проценты по кредиту за " + days + " дн.", newBalance);
                log.info("Начислены проценты по кредиту {} на сумму {}", ca.getAccountNumber(), interest);
            }
        }
    }

    public List<Transaction> getLastTransaction(String userId) {
        return transactionRepository.findTop10ByUserId(userId, Pageable.ofSize(10));
    }

    public List<Transaction> getAccountTransaction(String accountNumber) {
        return transactionRepository.findByAccountNumber(accountNumber);
    }

    private Account getAccount(String accountNumber) {
            return accountRepository.findByAccountNumber(accountNumber)
                    .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    private String generateAccountNumber() {
        String number;
        do {
            number = UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase();
        } while (accountRepository.existsByAccountNumber(number));
        return number;
    }

    private void validateSufficientFunds(Account account, BigDecimal amount) {
        if (account instanceof CreditAccount credit) {
            BigDecimal used = credit.getPrincipalDebit().add(credit.getAccruedInterest());
            BigDecimal available = credit.getCreditLimit().subtract(used);
            if (amount.compareTo(available) > 0) {
                throw new InsufficientFundsException("Недостаточно кредитного лимита. Доступно: " + available);
            }
        } else {
            if (account.getBalance().compareTo(amount) < 0) {
                throw new InsufficientFundsException("Недостаточно средств на счете");
            }
        }
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
