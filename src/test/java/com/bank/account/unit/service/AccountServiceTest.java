package com.bank.account.unit.service;

import com.bank.account.model.Account;
import com.bank.account.model.CheckingAccount;
import com.bank.account.model.TransactionType;
import com.bank.account.repository.AccountRepository;
import com.bank.account.repository.TransactionRepository;
import com.bank.account.service.AccountService;
import com.bank.account.service.TransactionService;
import com.bank.user.model.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock AccountRepository accountRepository;
    @Mock TransactionRepository transactionRepository;
    @Mock TransactionService transactionService;
    @InjectMocks AccountService accountService;

    private static final BigDecimal POSITIVE_BALANCE = new BigDecimal("1000");
    private static final BigDecimal NEGATIVE_BALANCE = new BigDecimal("-1000");
    private static final BigDecimal ZERO_BALANCE = BigDecimal.ZERO;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@example.com")
                .build();
    }

    @Test
    @DisplayName("Create checking account with positive balance should save account and create deposit transaction")
    void createCheckingAccount_positiveBalance_shouldSaveAccountAndCreateTransaction() {
        when(accountRepository.save(any(CheckingAccount.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CheckingAccount result = accountService.createCheckingAccount(user, POSITIVE_BALANCE);

        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getBalance()).isEqualByComparingTo(POSITIVE_BALANCE);

        verify(accountRepository).save(any(CheckingAccount.class));

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        ArgumentCaptor<BigDecimal> amountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        verify(transactionService).createTransaction(
                accountCaptor.capture(),
                amountCaptor.capture(),
                eq(TransactionType.DEPOSIT),
                eq("Initial deposit"),
                any(BigDecimal.class)
        );

        assertThat(accountCaptor.getValue()).isEqualTo(result);
        assertThat(amountCaptor.getValue()).isEqualByComparingTo(POSITIVE_BALANCE);
    }

    @Test
    @DisplayName("Create checking account with negative balance should throw IllegalArgumentException")
    void createCheckingAccount_negativeBalance_shouldThrowException() {
        assertThatThrownBy(() -> accountService.createCheckingAccount(user, NEGATIVE_BALANCE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("initial deposit must not be negative");

        verifyNoInteractions(accountRepository);
        verifyNoInteractions(transactionService);
    }

    @Test
    @DisplayName("Create checking account with zero balance should save account and not create deposit transaction")
    void createCheckingAccount_zeroBalance_shouldSaveAccountAndNotCreateTransaction() {
        when(accountRepository.save(any(CheckingAccount.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CheckingAccount result = accountService.createCheckingAccount(user, ZERO_BALANCE);

        assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(accountRepository).save(any(CheckingAccount.class));
        verifyNoInteractions(transactionService);
    }
}