package com.unity.payment_service.repository;

import com.unity.payment_service.entity.Payment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByUserIdAndBankAccountId(Long userId, Long bankAccountId, Pageable pageable);
}
