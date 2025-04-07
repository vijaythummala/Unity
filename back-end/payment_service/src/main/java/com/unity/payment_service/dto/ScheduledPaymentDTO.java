package com.unity.payment_service.dto;

import com.unity.payment_service.constants.PaymentType;
import com.unity.payment_service.constants.RecurringType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ScheduledPaymentDTO {

    private Long id;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Bank Account ID is required")
    private Long bankAccountId;

    @NotNull(message = "Payment type is required")
    private PaymentType paymentType;

    @NotNull(message = "Receiver details are required")
    private String receiverDetails;

    private String receiverBankAccount;

    private String receiverBankName;

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be greater than zero")
    private double amount;

    @NotNull(message = "Payment date is required")
    @Future(message = "Payment date must be in the future")
    private LocalDateTime paymentDate;

    @NotNull(message = "Recurring type is required")
    private RecurringType recurringType;
}
