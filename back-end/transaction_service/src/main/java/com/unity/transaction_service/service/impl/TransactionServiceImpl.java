package com.unity.transaction_service.service.impl;

import com.unity.transaction_service.client.AccountServiceClient;
import com.unity.transaction_service.constants.TransactionStatus;
import com.unity.transaction_service.constants.TransactionType;
import com.unity.transaction_service.dto.TransactionRequestDTO;
import com.unity.transaction_service.dto.NotificationDTO;
import com.unity.transaction_service.dto.TransactionDTO;
import com.unity.transaction_service.entity.Transaction;
import com.unity.transaction_service.exception.TransactionException;
import com.unity.transaction_service.kafka.NotificationProducer;
import com.unity.transaction_service.mapper.TransactionMapper;
import com.unity.transaction_service.repository.TransactionRepository;
import com.unity.transaction_service.service.TransactionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountServiceClient accountServiceClient;

    @Autowired
    private NotificationProducer notificationProducer;

    @Override
    public List<TransactionDTO> getTransactions(Long userId, Long bankAccountId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        List<Transaction> transactions = transactionRepository.findByUserIdAndBankAccountId(userId, bankAccountId, pageable);

        if (transactions.isEmpty()) {
            return new ArrayList<TransactionDTO>();
        }

        return transactions.stream().map(TransactionMapper::toDTO).toList();
    }

    @Override
    @Transactional
    public String createTransaction(TransactionRequestDTO request) {
        double currentBalance = accountServiceClient.getBalance(request.getUserId(), request.getBankAccountId());

        if (!(request.getType() == TransactionType.DEPOSIT) && currentBalance < request.getAmount()) {
            throw new TransactionException("Insufficient balance");
        }

        accountServiceClient.updateBalance(request.getUserId(), request.getBankAccountId(), 
            request.getType() == TransactionType.DEPOSIT ? request.getAmount() : -request.getAmount());

        Transaction transaction = new Transaction();
        transaction.setUserId(request.getUserId());
        transaction.setBankAccountId(request.getBankAccountId());
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setPaymentReferenceId(request.getPaymentReferenceId());

        transactionRepository.save(transaction);
        NotificationDTO event = new NotificationDTO(
                request.getUserId(),
                "transaction successful",
                "TRANSACTION");
        notificationProducer.sendNotification(event);

        return "Transaction successfully created.";
    }

    @Override
    @Transactional
    public String internalTransfer(Long userId, Long fromBankAccountId, Long toBankAccountId, double amount) {
        if (amount <= 0) {
            throw new TransactionException("Transfer amount must be greater than zero.");
        }

        double fromAccountBalance = accountServiceClient.getBalance(userId, fromBankAccountId);
        if (fromAccountBalance < amount) {
            throw new TransactionException("Insufficient balance in source account.");
        }


        accountServiceClient.updateBalance(userId, fromBankAccountId, -amount);


        accountServiceClient.updateBalance(userId, toBankAccountId, amount);


        Transaction debitTransaction = new Transaction();
        debitTransaction.setUserId(userId);
        debitTransaction.setBankAccountId(fromBankAccountId);
        debitTransaction.setAmount(amount);
        debitTransaction.setType(TransactionType.INTERNAL_TRANSFER_DEBIT);
        debitTransaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(debitTransaction);

        Transaction creditTransaction = new Transaction();
        creditTransaction.setUserId(userId);
        creditTransaction.setBankAccountId(toBankAccountId);
        creditTransaction.setAmount(amount);
        creditTransaction.setType(TransactionType.INTERNAL_TRANSFER_CREDIT);
        creditTransaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(creditTransaction);

        NotificationDTO event = new NotificationDTO(
                userId,
                "Internal transfer succesfull",
                "TRANSACTION");
        notificationProducer.sendNotification(event);
        return "Internal transfer completed successfully.";
    }
}
