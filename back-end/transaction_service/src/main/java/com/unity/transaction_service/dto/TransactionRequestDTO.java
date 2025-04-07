package com.unity.transaction_service.dto;

import com.unity.transaction_service.constants.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionRequestDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Bank account ID is required")
    private Long bankAccountId;

    @NotNull(message = "Transaction type is required")
    private TransactionType type;

    @NotNull(message = "Amount is required")
    @Min(value = 0, message = "Amount must be greater than zero")
    private double amount;

    private String paymentReferenceId;
}
