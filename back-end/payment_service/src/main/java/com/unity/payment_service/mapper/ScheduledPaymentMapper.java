package com.unity.payment_service.mapper;

import com.unity.payment_service.dto.ScheduledPaymentDTO;
import com.unity.payment_service.entity.ScheduledPayment;

public class ScheduledPaymentMapper {

    public static ScheduledPaymentDTO toDTO(ScheduledPayment payment) {
        if (payment == null) return null;

        ScheduledPaymentDTO dto = new ScheduledPaymentDTO();
        dto.setId(payment.getId());
        dto.setUserId(payment.getUserId());
        dto.setBankAccountId(payment.getBankAccountId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentType(payment.getPaymentType());
        dto.setReceiverDetails(payment.getReceiverDetails());
        dto.setReceiverBankAccount(payment.getReceiverBankAccount());
        dto.setReceiverBankName(payment.getReceiverBankName());
        dto.setRecurringType(payment.getRecurringType());
        dto.setPaymentDate(payment.getPaymentDate());

        return dto;
    }

    public static ScheduledPayment toEntity(ScheduledPaymentDTO dto) {
        if (dto == null) return null;

        ScheduledPayment payment = new ScheduledPayment();
        payment.setUserId(dto.getUserId());
        payment.setBankAccountId(dto.getBankAccountId());
        payment.setAmount(dto.getAmount());
        payment.setPaymentType(dto.getPaymentType());
        payment.setReceiverDetails(dto.getReceiverDetails());
        payment.setReceiverBankAccount(dto.getReceiverBankAccount());
        payment.setReceiverBankName(dto.getReceiverBankName());
        payment.setRecurringType(dto.getRecurringType());
        payment.setPaymentDate(dto.getPaymentDate());

        return payment;
    }
}
