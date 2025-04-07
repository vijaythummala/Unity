package com.unity.account_service.service.impl;

import com.unity.account_service.client.UserServiceClient;
import com.unity.account_service.constants.*;
import com.unity.account_service.dto.*;
import com.unity.account_service.entity.AccountRequest;
import com.unity.account_service.entity.BankAccount;
import com.unity.account_service.exception.AccountException;
import com.unity.account_service.kafka.NotificationProducer;
import com.unity.account_service.mapper.AccountRequestMapper;
import com.unity.account_service.mapper.BankAccountMapper;
import com.unity.account_service.repository.AccountRequestRepository;
import com.unity.account_service.repository.BankAccountRepository;
import com.unity.account_service.service.AccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRequestRepository accountRequestRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private NotificationProducer notificationProducer;

    private static final long START_ACCOUNT_NUMBER = 100000000001L;

    @Override
    @Transactional
    public String createAccountRequest(AccountRequestDTO accountRequestDTO) {

        if (accountRequestDTO.getUserId() == null || accountRequestDTO.getAccountType() == null
                || accountRequestDTO.getRequestType() == null) {
            throw new AccountException("User ID, account type, and request type are required.");
        }

        if (accountRequestDTO.getRequestType() != RequestType.NEW_BANK_ACCOUNT
                && accountRequestDTO.getBankAccountId() == null) {
            throw new AccountException("Bank account ID is required for closing or suspending an account.");
        }

        AccountRequest request = AccountRequestMapper.toEntity(accountRequestDTO);
        request.setStatus(AccountRequestStatus.PENDING);

        accountRequestRepository.save(request);
        return "Account request submitted successfully.";
    }

    @Override
    public List<AccountRequestDTO> getPendingRequests(int page, int limit) {
        List<AccountRequest> requests = accountRequestRepository.findPendingRequests(page, limit);
        if (requests.isEmpty()) {
            return new ArrayList<AccountRequestDTO>();
        }
        return requests.stream().map(AccountRequestMapper::toDTO).toList();
    }

    @Override
    @Transactional
    public String approveAccountRequest(Long requestId, Long adminId) {
        UserDTO admin = userServiceClient.getUserById(adminId);
        if (admin == null || admin.getRole() != Role.ADMIN) {
            throw new AccountException("Only admins can approve requests.");
        }

        AccountRequest request = accountRequestRepository.findById(requestId)
                .orElseThrow(() -> new AccountException("Account request not found."));

        request.setStatus(AccountRequestStatus.APPROVED);

        if (request.getRequestType() == RequestType.NEW_BANK_ACCOUNT) {
            String accountNumber = generateNewAccountNumber();
            BankAccount account = new BankAccount();
            account.setUserId(request.getUserId());
            account.setAccountNumber(accountNumber);
            account.setAccountType(request.getAccountType());
            account.setStatus(AccountStatus.ACTIVE);
            account.setBalance(0.0);
            bankAccountRepository.save(account);
            NotificationDTO event = new NotificationDTO(
                    request.getUserId(),
                    "account created",
                    "ACCOUNT");
            notificationProducer.sendNotification(event);
        } else {
            BankAccount account = bankAccountRepository.findById(request.getBankAccountId())
                    .orElseThrow(() -> new AccountException("Bank account not found."));
            String message = "";
            if (request.getRequestType() == RequestType.CLOSE_BANK_ACCOUNT) {
                account.setStatus(AccountStatus.CLOSED);
                message = "closed";
            } else if (request.getRequestType() == RequestType.SUSPEND_BANK_ACCOUNT) {
                account.setStatus(AccountStatus.SUSPENDED);
                message = "suspended";
            }
            bankAccountRepository.save(account);
            NotificationDTO event = new NotificationDTO(
                    request.getUserId(),
                    message,
                    "ACCOUNT");
            notificationProducer.sendNotification(event);
        }

        accountRequestRepository.save(request);
        return "Request approved successfully.";
    }

    private String generateNewAccountNumber() {
        Optional<String> lastAccount = bankAccountRepository.findLastAccountNumber();
        long newNumber = lastAccount.map(Long::parseLong).orElse(START_ACCOUNT_NUMBER - 1) + 1;
        return String.valueOf(newNumber);
    }

    @Override
    public String rejectAccountRequest(Long requestId, Long adminId) {
        UserDTO admin = userServiceClient.getUserById(adminId);
        if (admin == null || admin.getRole() != Role.ADMIN) {
            throw new AccountException("Only admins can approve requests.");
        }

        AccountRequest request = accountRequestRepository.findById(requestId)
                .orElseThrow(() -> new AccountException("Account request not found."));

        request.setStatus(AccountRequestStatus.REJECTED);
        accountRequestRepository.save(request);
        return "Request rejected successfully";
    }

    @Override
    public List<BankAccountDTO> getUserAccounts(Long userId, AccountStatus status, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        return bankAccountRepository.findByUserIdAndStatus(userId, status, pageable)
                .map(BankAccountMapper::toDTO)
                .getContent();
    }

    @Override
    public BankAccountDTO getPrimaryBankAccount(Long userId) {
        return BankAccountMapper.toDTO(
                bankAccountRepository.findFirstByUserId(userId)
                        .orElseThrow(() -> new AccountException("No bank account found for this user.")));
    }

    @Override
    public double getBalance(Long userId, Long bankAccountId) {
        return bankAccountRepository.findBalanceByUserIdAndAccountId(userId, bankAccountId)
                .orElseThrow(() -> new AccountException("Bank account not found."));
    }

    @Override
    public String updateBalance(Long userId, Long bankAccountId, double amount) {
        BankAccount account = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new AccountException("Bank account not found."));

        if (account.getBalance() + amount < 0) {
            throw new AccountException("Amount must be greater than zero.");
        }

        account.setBalance(account.getBalance() + amount);
        bankAccountRepository.save(account);
        return "Balance updated successfully.";
    }

    @Override
    public BankAccountDTO getActiveBankAccount(Long userId, Long bankAccountId) {
        BankAccount account = bankAccountRepository
                .findByUserIdAndIdAndStatus(userId, bankAccountId, AccountStatus.ACTIVE)
                .orElseThrow(
                        () -> new AccountException("No active bank account found for the given user and account ID."));

        return BankAccountMapper.toDTO(account);
    }
}
