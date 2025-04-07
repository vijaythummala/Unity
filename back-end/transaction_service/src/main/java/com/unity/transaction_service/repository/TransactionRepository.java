package com.unity.transaction_service.repository;

import com.unity.transaction_service.entity.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserIdAndBankAccountId(Long userId, Long bankAccountId, Pageable pageable);
}
