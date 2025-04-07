package com.unity.transaction_service.mapper;

import com.unity.transaction_service.dto.TransactionDTO;
import com.unity.transaction_service.entity.Transaction;
import java.time.format.DateTimeFormatter;

public class TransactionMapper {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static TransactionDTO toDTO(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setUserId(transaction.getUserId());
        dto.setBankAccountId(transaction.getBankAccountId());
        dto.setAmount(transaction.getAmount());
        dto.setType(transaction.getType());
        dto.setStatus(transaction.getStatus());
        dto.setPaymentReferenceId(transaction.getPaymentReferenceId());
        dto.setCreatedAt(transaction.getCreatedAt().format(formatter));

        return dto;
    }

    public static Transaction toEntity(TransactionDTO dto) {
        if (dto == null) {
            return null;
        }

        Transaction transaction = new Transaction();
        transaction.setUserId(dto.getUserId());
        transaction.setBankAccountId(dto.getBankAccountId());
        transaction.setAmount(dto.getAmount());
        transaction.setType(dto.getType());
        transaction.setStatus(dto.getStatus());
        transaction.setPaymentReferenceId(dto.getPaymentReferenceId());

        return transaction;
    }
}
