package com.unity.account_service.repository;

import com.unity.account_service.entity.BankAccount;
import com.unity.account_service.constants.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    @Query("SELECT MAX(b.accountNumber) FROM BankAccount b")
    Optional<String> findLastAccountNumber();

    Optional<BankAccount> findFirstByUserId(Long userId);

    @Query("SELECT b.balance FROM BankAccount b WHERE b.userId = :userId AND b.id = :bankAccountId")
    Optional<Double> findBalanceByUserIdAndAccountId(Long userId, Long bankAccountId);

    Page<BankAccount> findByUserIdAndStatus(Long userId, AccountStatus status, Pageable pageable);

    Optional<BankAccount> findByUserIdAndIdAndStatus(Long userId, Long bankAccountId, AccountStatus status);

}
