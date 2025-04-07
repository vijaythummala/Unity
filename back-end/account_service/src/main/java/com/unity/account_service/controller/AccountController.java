package com.unity.account_service.controller;

import com.unity.account_service.constants.AccountStatus;
import com.unity.account_service.dto.*;
import com.unity.account_service.service.AccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/request")
    public ResponseEntity<String> requestAccount(@RequestBody AccountRequestDTO accountRequestDTO) {
        String response = accountService.createAccountRequest(accountRequestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/requests/pending")
    public ResponseEntity<List<AccountRequestDTO>> getPendingRequests(@RequestParam int page, @RequestParam int limit) {
        List<AccountRequestDTO> requests = accountService.getPendingRequests(page, limit);
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/approveRequest")
    public ResponseEntity<String> approveRequest(@RequestParam Long requestId, @RequestParam Long adminId) {
        String response = accountService.approveAccountRequest(requestId, adminId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/rejectRequest")
    public ResponseEntity<String> rejectRequest(@RequestParam Long requestId, @RequestParam Long adminId) {
        String response = accountService.rejectAccountRequest(requestId, adminId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getUserAccounts")
    public ResponseEntity<List<BankAccountDTO>> getUserAccounts(
            @RequestParam Long userId,
            @RequestParam AccountStatus status,
            @RequestParam int page,
            @RequestParam int limit) {
        
        List<BankAccountDTO> accounts = accountService.getUserAccounts(userId, status, page, limit);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/getPrimaryAccount")
    public ResponseEntity<BankAccountDTO> getUserPrimaryAccount(@RequestParam Long userId) {
        BankAccountDTO account = accountService.getPrimaryBankAccount(userId);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/getBalance")
    public ResponseEntity<Double> getBalance(@RequestParam Long userId, @RequestParam Long bankAccountId) {
        double balance = accountService.getBalance(userId, bankAccountId);
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/updateBalance")
    public ResponseEntity<String> updateBalance(
            @RequestParam Long userId,
            @RequestParam Long bankAccountId,
            @RequestParam double amount) {
        
        String response = accountService.updateBalance(userId, bankAccountId, amount);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getBankAccount")
    public ResponseEntity<BankAccountDTO> getActiveBankAccount(@RequestParam Long userId, @RequestParam Long bankAccountId) {
        BankAccountDTO account = accountService.getActiveBankAccount(userId, bankAccountId);
        return ResponseEntity.ok(account);
    }
}
