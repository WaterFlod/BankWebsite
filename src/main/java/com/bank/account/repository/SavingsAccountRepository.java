package com.bank.account.repository;

import com.bank.account.model.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, Long> {
    List<SavingsAccount> findByLastInterestDateBefore(LocalDate date);
}
