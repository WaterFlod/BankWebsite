package com.bank.account.repository;

import com.bank.account.model.Account;
import com.bank.account.model.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByAccount(Account account);

    @Query("SELECT t FROM Transaction t " +
           "JOIN t.account a " +
           "JOIN a.user u " +
           "WHERE u.id = :userId " +
           "ORDER BY t.timestamp DESC")
    List<Transaction> findTop10ByUserId(@Param("userId") String userId, Pageable pageable);
}
