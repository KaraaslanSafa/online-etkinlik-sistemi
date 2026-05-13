package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.UserRole;

public interface UserService {
    
    // CRUD Operations
    UserDTO createUser(UserDTO userDTO);
    UserDTO getUserById(Long id);
    UserDTO getUserByUsername(String username);
    UserDTO getUserByEmail(String email);
    List<UserDTO> getAllUsers();
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    void softDeleteUser(Long id, String reason);
    
    // Pagination
    Page<UserDTO> getAllUsersPaginated(Pageable pageable);
    Page<UserDTO> getUsersByRole(UserRole role, Pageable pageable);
    Page<UserDTO> searchUsers(String name, Pageable pageable);
    
    // Verification & Password Reset
    void verifyEmail(Long userId);
    void verifyPhone(Long userId);
    boolean isEmailVerified(Long userId);
    boolean verifyOtp(String email, String otp);
    String forgotPassword(String email);
    void resetPassword(String email, String otp, String newPassword);
    
    // Authentication
    UserDetails loadUserByUsername(String username);
    boolean checkPasswordMatch(String rawPassword, String encodedPassword);
    String encodePassword(String password);
    
    // Role Management
    void assignRole(Long userId, String roleName);
    void removeRole(Long userId, String roleName);
    
    // Other operations
    void updateLastLogin(Long userId);
    List<UserDTO> getUsersByRole(UserRole role);
    Optional<UserDTO> findByUsernameOrEmail(String username, String email);
}
