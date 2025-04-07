package com.unity.account_service.mapper;

import com.unity.account_service.dto.BankAccountDTO;
import com.unity.account_service.entity.BankAccount;

public class BankAccountMapper {

    public static BankAccountDTO toDTO(BankAccount account) {
        if (account == null) {
            return null;
        }

        BankAccountDTO dto = new BankAccountDTO();
        dto.setId(account.getId());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setUserId(account.getUserId());
        dto.setAccountType(account.getAccountType());
        dto.setStatus(account.getStatus());
        dto.setBalance(account.getBalance());
        dto.setCurrency(account.getCurrency());

        return dto;
    }

    public static BankAccount toEntity(BankAccountDTO dto) {
        if (dto == null) {
            return null;
        }

        BankAccount account = new BankAccount();
        account.setAccountNumber(dto.getAccountNumber());
        account.setUserId(dto.getUserId());
        account.setAccountType(dto.getAccountType());
        account.setStatus(dto.getStatus());
        account.setBalance(dto.getBalance());
        account.setCurrency(dto.getCurrency());

        return account;
    }
}
