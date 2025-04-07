package com.unity.account_service.service;

import com.unity.account_service.constants.AccountStatus;
import com.unity.account_service.dto.AccountRequestDTO;
import com.unity.account_service.dto.BankAccountDTO;

import java.util.List;

public interface AccountService {

    String createAccountRequest(AccountRequestDTO accountRequestDTO);

    List<AccountRequestDTO> getPendingRequests(int page, int limit);

    String approveAccountRequest(Long requestId, Long adminId);

    String rejectAccountRequest(Long requestId, Long adminId);

    List<BankAccountDTO> getUserAccounts(Long userId, AccountStatus status, int page, int limit);

    BankAccountDTO getPrimaryBankAccount(Long userId);

    double getBalance(Long userId, Long bankAccountId);

    String updateBalance(Long userId, Long bankAccountId, double amount);

    BankAccountDTO getActiveBankAccount(Long userId, Long bankAccountId);
}
