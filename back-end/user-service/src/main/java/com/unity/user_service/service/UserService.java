package com.unity.user_service.service;

import com.unity.user_service.dto.LoginRequestDTO;
import com.unity.user_service.dto.UserDTO;
import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDTO registerUser(UserDTO userDTO);
    Optional<UserDTO> getUserById(Long id);
    List<UserDTO> getAllUsers();
    UserDTO updateUser(Long id, UserDTO userDTO);
    void softDeleteUser(Long id);
    void restoreUser(Long id);
    UserDTO loginUser(LoginRequestDTO loginRequest);
    void approveOrRejectUser(List<Long> userId, Long adminId,String action, String comments);
    void deactivateUser(Long userId, Long adminId);
}
