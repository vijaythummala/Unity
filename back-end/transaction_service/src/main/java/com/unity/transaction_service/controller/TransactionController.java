package com.unity.transaction_service.controller;

import com.unity.transaction_service.dto.TransactionRequestDTO;
import com.unity.transaction_service.dto.TransactionDTO;
import com.unity.transaction_service.service.TransactionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/fetchTransactions")
    public ResponseEntity<List<TransactionDTO>> getTransactions(
            @RequestParam Long userId, 
            @RequestParam Long bankAccountId, 
            @RequestParam int page, 
            @RequestParam int limit) {
        
        List<TransactionDTO> transactions = transactionService.getTransactions(userId, bankAccountId, page, limit);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createTransaction(@RequestBody TransactionRequestDTO request) {
        String response = transactionService.createTransaction(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/internalTransfer")
    public ResponseEntity<String> internalTransfer(
            @RequestParam Long userId, 
            @RequestParam Long fromBankAccountId, 
            @RequestParam Long toBankAccountId, 
            @RequestParam double amount) {
        
        String response = transactionService.internalTransfer(userId, fromBankAccountId, toBankAccountId, amount);
        return ResponseEntity.ok(response);
    }
}
