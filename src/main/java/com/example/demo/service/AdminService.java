package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.AdminDTO;

public interface AdminService {
    
    // Admin yönetimi
    AdminDTO createAdmin(AdminDTO adminDTO);
    AdminDTO getAdminById(Long id);
    AdminDTO getAdminByUsername(String username);
    AdminDTO getAdminByEmail(String email);
    List<AdminDTO> getAllAdmins();
    AdminDTO updateAdmin(Long id, AdminDTO adminDTO);
    void deleteAdmin(Long id);
    
    // Admin işlemleri
    void recordApproval(Long adminId);
    void recordRejection(Long adminId);
    void recordDeletion(Long adminId);
    void updateLastLogin(Long adminId);
    
    // Admin doğrulama
    boolean verifyAdminExists(Long adminId);
    boolean isAdmin(Long adminId);
}
