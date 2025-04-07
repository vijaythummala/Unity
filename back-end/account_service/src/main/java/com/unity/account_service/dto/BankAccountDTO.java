package com.unity.account_service.dto;

import com.unity.account_service.constants.AccountStatus;
import com.unity.account_service.constants.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankAccountDTO {

    private Long id;

    @NotNull(message = "Account number is required")
    private String accountNumber;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    @NotNull(message = "Status is required")
    private AccountStatus status;

    @NotNull(message = "Balance is required")
    private double balance;

    private String currency = "USD"; // Fixed to USD
}
