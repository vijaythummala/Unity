package com.unity.user_service.service.impl;

import com.unity.user_service.constants.Role;
import com.unity.user_service.constants.Status;
import com.unity.user_service.dto.LoginRequestDTO;
import com.unity.user_service.dto.UserDTO;
import com.unity.user_service.entity.User;
import com.unity.user_service.exception.UserException;
import com.unity.user_service.mapper.UserMapper;
import com.unity.user_service.repository.UserRepository;
import com.unity.user_service.service.UserService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDTO registerUser(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new UserException("Username is already taken.");
        }

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new UserException("Email is already in use.");
        }

        userDTO.setStatus(Status.PENDING);
        userDTO.setRole(Role.USER);
        User user = UserMapper.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        user = userRepository.save(user);
        return UserMapper.toDTO(user);
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setEmail(userDTO.getEmail());
                    existingUser.setPhoneNumber(userDTO.getPhoneNumber());
                    userRepository.save(existingUser);
                    return UserMapper.toDTO(existingUser);
                }).orElseThrow(() -> new UserException("User not found!"));
    }

    @Override
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .filter(user -> !user.isDeleted())
                .map(UserMapper::toDTO);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAllActiveUsers()
                .stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void softDeleteUser(Long id) {
        userRepository.softDeleteUser(id);
    }

    @Override
    @Transactional
    public void restoreUser(Long id) {
        userRepository.findById(id)
                .ifPresent(user -> {
                    user.setDeleted(false);
                    userRepository.save(user);
                });
    }

    @Override
    public UserDTO loginUser(LoginRequestDTO loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UserException("Invalid username or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new UserException("Invalid username or password");
        }

        if (user.getRole() == Role.USER && user.getStatus() != Status.ACTIVE) {
            throw new UserException("User Not Active");
        }

        return UserMapper.toDTO(user);
    }

    @Override
    public void approveOrRejectUser(List<Long> userIds, Long adminId, String action, String comments) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new UserException("Admin not found"));

        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new UserException("Only admins can approve users!");
        }

        for (Long userId : userIds) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserException("User not found with ID: " + userId));
    
            if (StringUtils.equals(action, "APPROVE")) {
                user.setStatus(Status.ACTIVE);
            } else if (StringUtils.equals(action, "REJECT")) {
                user.setStatus(Status.SUSPENDED);
            } else {
                throw new UserException("Invalid action: " + action);
            }
    
            user.setActionBy(admin.getUsername());
            user.setComments(comments);
            userRepository.save(user);
        }
    }

    @Override
    public void deactivateUser(Long userId, Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new UserException("Admin not found"));

        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new UserException("Only admins can deactivate users!");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found"));

        user.setStatus(Status.SUSPENDED);
        userRepository.save(user);
    }

}
