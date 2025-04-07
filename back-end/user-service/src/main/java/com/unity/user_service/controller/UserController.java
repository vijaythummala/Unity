package com.unity.user_service.controller;

import com.unity.user_service.dto.LoginRequestDTO;
import com.unity.user_service.dto.UserDTO;
import com.unity.user_service.dto.BulkActionRequestDTO;
import com.unity.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.registerUser(userDTO);
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping("/getUserById")
    public ResponseEntity<UserDTO> getUserById(@RequestParam Long id) {
        Optional<UserDTO> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/updateUser")
    public ResponseEntity<UserDTO> updateUser(@RequestParam Long id, @RequestBody UserDTO userDTO) {
        try {
            UserDTO updatedUser = userService.updateUser(id, userDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/softDeleteUser")
    public ResponseEntity<?> softDeleteUser(@RequestParam Long id) {
        userService.softDeleteUser(id);
        return ResponseEntity.ok("User deleted successfully!");
    }

    @PutMapping("/restoreUser")
    public ResponseEntity<?> restoreUser(@RequestParam Long id) {
        userService.restoreUser(id);
        return ResponseEntity.ok("User restored successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginRequestDTO loginRequest) {
            UserDTO user = userService.loginUser(loginRequest);
            return ResponseEntity.ok(user);
    }

    @PutMapping("/approveOrRejectUsers")
    public ResponseEntity<String> approveUsers(@RequestBody BulkActionRequestDTO request) {
        userService.approveOrRejectUser(request.getUserIds(), request.getAdminId(), request.getAction(), request.getComments());
        return ResponseEntity.ok("Users processed successfully!");
    }

    @PutMapping("/deactivateUser")
    public ResponseEntity<String> deactivateUser(@RequestParam Long userId, @RequestParam Long adminId) {
        userService.deactivateUser(userId, adminId);
        return ResponseEntity.ok("User login deactivated!");
    }

}
