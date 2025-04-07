package com.unity.transaction_service.service;

import com.unity.transaction_service.dto.TransactionRequestDTO;
import com.unity.transaction_service.dto.TransactionDTO;

import java.util.List;

public interface TransactionService {

    List<TransactionDTO> getTransactions(Long userId, Long bankAccountId, int page, int limit);

    String createTransaction(TransactionRequestDTO request);

    String internalTransfer(Long userId, Long fromBankAccountId, Long toBankAccountId, double amount);
}
