package com.unity.payment_service.service;

import com.unity.payment_service.dto.PaymentDTO;
import com.unity.payment_service.dto.ScheduledPaymentDTO;

import java.util.List;

public interface PaymentService {

    String createPayment(PaymentDTO paymentDTO);

    String createScheduledPayment(ScheduledPaymentDTO scheduledPaymentDTO);

    List<PaymentDTO> getPayments(Long userId, Long bankAccountId, int page, int limit);

    List<ScheduledPaymentDTO> getScheduledPayments(Long userId, Long bankAccountId, int page, int limit);

    String deleteScheduledPayment(Long userId, Long bankAccountId, Long paymentId);
}
