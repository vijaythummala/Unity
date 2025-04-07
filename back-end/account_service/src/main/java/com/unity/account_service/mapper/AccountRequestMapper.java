package com.unity.account_service.mapper;

import com.unity.account_service.dto.AccountRequestDTO;
import com.unity.account_service.entity.AccountRequest;

public class AccountRequestMapper {

    public static AccountRequestDTO toDTO(AccountRequest request) {
        if (request == null) {
            return null;
        }

        AccountRequestDTO dto = new AccountRequestDTO();
        dto.setId(request.getId());
        dto.setUserId(request.getUserId());
        dto.setAccountType(request.getAccountType());
        dto.setStatus(request.getStatus());
        dto.setRequestType(request.getRequestType());
        dto.setBankAccountId(request.getBankAccountId());

        return dto;
    }

    public static AccountRequest toEntity(AccountRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        AccountRequest request = new AccountRequest();
        request.setUserId(dto.getUserId());
        request.setAccountType(dto.getAccountType());
        request.setStatus(dto.getStatus());
        request.setRequestType(dto.getRequestType());
        request.setBankAccountId(dto.getBankAccountId());
        
        return request;
    }
}
