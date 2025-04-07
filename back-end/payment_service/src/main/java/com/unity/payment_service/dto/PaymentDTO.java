package com.unity.payment_service.dto;

import com.unity.payment_service.constants.PaymentStatus;
import com.unity.payment_service.constants.PaymentType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentDTO {

    private Long id;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Bank account ID is required")
    private Long bankAccountId;

    @NotNull(message = "Payment type is required")
    private PaymentType paymentType;

    @NotNull(message = "Receiver details are required")
    private String receiverDetails;

    private String receiverBankAccount;

    private String receiverBankName;

    @NotNull(message = "Amount is required")
    @Min(value = 0, message = "Amount must be greater than zero")
    private double amount;

    private PaymentStatus status;

    private LocalDateTime paymentDate;
}
