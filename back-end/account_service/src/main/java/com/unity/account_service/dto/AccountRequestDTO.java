package com.unity.account_service.dto;

import com.unity.account_service.constants.AccountRequestStatus;
import com.unity.account_service.constants.AccountType;
import com.unity.account_service.constants.RequestType;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountRequestDTO {

    private Long id;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    @NotNull(message = "Request type is required")
    private RequestType requestType;

    private Long bankAccountId;

    private AccountRequestStatus status;
}
