package com.unity.account_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.unity.account_service.dto.UserDTO;

@FeignClient(name = "user-service", path = "/users")
public interface UserServiceClient {
    
    @GetMapping("/getUserById")
    UserDTO getUserById(@RequestParam Long id);
}
