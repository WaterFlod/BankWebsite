package com.bank.account.repository;

import com.bank.account.model.CreditAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CreditAccountRepository extends JpaRepository<CreditAccount, Long> {
    List<CreditAccount> findByLastInterestDateBefore(LocalDate date);
}
