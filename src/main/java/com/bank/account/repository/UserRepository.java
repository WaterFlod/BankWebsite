package com.bank.account.repository;

import com.bank.account.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Query("SELECT u FROM User u WHERE u.email = :login OR u.phoneNumber = :login")
    Optional<User> findByEmailOrPhoneNumber(@Param("login")String email, @Param("login")String phoneNumber);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String PhoneNumber);
}
