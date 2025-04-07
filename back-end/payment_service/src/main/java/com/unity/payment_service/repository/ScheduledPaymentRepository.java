package com.unity.payment_service.repository;

import com.unity.payment_service.entity.ScheduledPayment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ScheduledPaymentRepository extends JpaRepository<ScheduledPayment, Long> {

    List<ScheduledPayment> findByUserIdAndBankAccountId(Long userId, Long bankAccountId, Pageable pageable);

    Optional<ScheduledPayment> findByIdAndUserIdAndBankAccountId(Long id, Long userId, Long bankAccountId);
}
