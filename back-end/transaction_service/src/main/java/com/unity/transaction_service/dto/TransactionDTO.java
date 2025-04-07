package com.unity.transaction_service.dto;

import com.unity.transaction_service.constants.TransactionStatus;
import com.unity.transaction_service.constants.TransactionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionDTO {
    
    private Long id;
    private Long userId;
    private Long bankAccountId;
    private double amount;
    private TransactionType type;
    private TransactionStatus status;
    private String paymentReferenceId;
    private String createdAt;
}
