package com.unity.payment_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "account-service", path = "/api/accounts")
public interface TransactionServiceClient {

    @GetMapping("/getBalance")
    Double getBalance(@RequestParam Long userId, @RequestParam Long bankAccountId);

    @PostMapping("/updateBalance")
    String updateBalance(
            @RequestParam Long userId,
            @RequestParam Long bankAccountId,
            @RequestParam double amount);
}

