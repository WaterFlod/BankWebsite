package com.bank.account.unit.service;

import com.bank.account.model.CheckingAccount;
import com.bank.account.model.TransactionType;
import com.bank.account.repository.AccountRepository;
import com.bank.account.repository.TransactionRepository;
import com.bank.account.service.AccountService;
import com.bank.user.model.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock AccountRepository accountRepository;
    @Mock TransactionRepository transactionRepository;
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
        CheckingAccount savedAccount = mock(CheckingAccount.class);
        when(accountRepository.save(any(CheckingAccount.class))).thenReturn(savedAccount);
        when(savedAccount.getBalance()).thenReturn(POSITIVE_BALANCE);

        CheckingAccount result = accountService.createCheckingAccount(user, POSITIVE_BALANCE);

        assertThat(result).isEqualTo(savedAccount);
        verify(accountRepository).save(any(CheckingAccount.class));
        verify(transactionRepository).save(argThat(transaction ->
                transaction.getType() == TransactionType.DEPOSIT &&
                transaction.getAmount().compareTo(POSITIVE_BALANCE) == 0
        ));
    }

    @Test
    @DisplayName("Create checking account with negative balance should throw IllegalArgumentException")
    void createCheckingAccount_negativeBalance_shouldThrowException() {
        assertThatThrownBy(() -> accountService.createCheckingAccount(user, NEGATIVE_BALANCE))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("The initial deposit must not be negative");
    }

    @Test
    @DisplayName("Create checking account with zero balance should save account and not create deposit transaction")
    void createCheckingAccount_zeroBalance_shouldSaveAccountAndNotCreateTransaction() {
        CheckingAccount savedAccount = mock(CheckingAccount.class);
        when(accountRepository.save(any(CheckingAccount.class))).thenReturn(savedAccount);
        when(savedAccount.getBalance()).thenReturn(ZERO_BALANCE);

        CheckingAccount result = accountService.createCheckingAccount(user, ZERO_BALANCE);

        assertThat(result).isEqualTo(savedAccount);
        verify(accountRepository).save(any(CheckingAccount.class));
        verify(accountService, never()).createTransaction(any(), any(), any(), any(), any());
    }
}