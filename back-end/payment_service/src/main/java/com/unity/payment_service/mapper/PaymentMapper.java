package com.unity.payment_service.mapper;

import com.unity.payment_service.dto.PaymentDTO;
import com.unity.payment_service.entity.Payment;

public class PaymentMapper {

    public static PaymentDTO toDTO(Payment payment) {
        if (payment == null) return null;

        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setUserId(payment.getUserId());
        dto.setBankAccountId(payment.getBankAccountId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentType(payment.getPaymentType());
        dto.setReceiverDetails(payment.getReceiverDetails());
        dto.setReceiverBankAccount(payment.getReceiverBankAccount());
        dto.setReceiverBankName(payment.getReceiverBankName());
        dto.setStatus(payment.getStatus());
        dto.setPaymentDate(payment.getPaymentDate());

        return dto;
    }

    public static Payment toEntity(PaymentDTO dto) {
        if (dto == null) return null;

        Payment payment = new Payment();
        payment.setUserId(dto.getUserId());
        payment.setBankAccountId(dto.getBankAccountId());
        payment.setAmount(dto.getAmount());
        payment.setPaymentType(dto.getPaymentType());
        payment.setReceiverDetails(dto.getReceiverDetails());
        payment.setReceiverBankAccount(dto.getReceiverBankAccount());
        payment.setReceiverBankName(dto.getReceiverBankName());
        payment.setStatus(dto.getStatus());
        payment.setPaymentDate(dto.getPaymentDate());

        return payment;
    }
}
