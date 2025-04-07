package com.unity.payment_service.service.impl;

import com.unity.payment_service.dto.PaymentDTO;
import com.unity.payment_service.dto.ScheduledPaymentDTO;
import com.unity.payment_service.entity.Payment;
import com.unity.payment_service.entity.ScheduledPayment;
import com.unity.payment_service.exception.PaymentException;
import com.unity.payment_service.mapper.PaymentMapper;
import com.unity.payment_service.mapper.ScheduledPaymentMapper;
import com.unity.payment_service.repository.PaymentRepository;
import com.unity.payment_service.repository.ScheduledPaymentRepository;
import com.unity.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    ScheduledPaymentRepository scheduledPaymentRepository;

    @Override
    @Transactional
    public String createPayment(PaymentDTO paymentDTO) {
        Payment payment = PaymentMapper.toEntity(paymentDTO);
        paymentRepository.save(payment);
        return "Payment created successfully.";
    }

    @Override
    @Transactional
    public String createScheduledPayment(ScheduledPaymentDTO scheduledPaymentDTO) {
        ScheduledPayment scheduledPayment = ScheduledPaymentMapper.toEntity(scheduledPaymentDTO);
        scheduledPaymentRepository.save(scheduledPayment);
        return "Scheduled payment created successfully.";
    }

    @Override
    public List<PaymentDTO> getPayments(Long userId, Long bankAccountId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        List<Payment> payments = paymentRepository.findByUserIdAndBankAccountId(userId, bankAccountId, pageable);

        if (payments.isEmpty()) {
            throw new PaymentException("No payments found.");
        }

        return payments.stream().map(PaymentMapper::toDTO).toList();
    }

    @Override
    public List<ScheduledPaymentDTO> getScheduledPayments(Long userId, Long bankAccountId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        List<ScheduledPayment> scheduledPayments = scheduledPaymentRepository.findByUserIdAndBankAccountId(userId, bankAccountId, pageable);

        if (scheduledPayments.isEmpty()) {
            throw new PaymentException("No scheduled payments found.");
        }

        return scheduledPayments.stream().map(ScheduledPaymentMapper::toDTO).toList();
    }

    @Override
    @Transactional
    public String deleteScheduledPayment(Long userId, Long bankAccountId, Long paymentId) {
        ScheduledPayment scheduledPayment = scheduledPaymentRepository.findByIdAndUserIdAndBankAccountId(paymentId, userId, bankAccountId)
                .orElseThrow(() -> new PaymentException("Scheduled payment not found."));

        scheduledPayment.setDeleted(true);
        scheduledPaymentRepository.save(scheduledPayment);

        return "Scheduled payment deleted successfully.";
    }
}
