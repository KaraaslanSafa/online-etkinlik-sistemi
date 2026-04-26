package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.dto.AdminDTO;
import com.example.demo.entity.Admin;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AdminRepository;

@Service
public class AdminServiceImpl implements AdminService {
    
    private final AdminRepository adminRepository;
    
    public AdminServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }
    
    @Override
    public AdminDTO createAdmin(AdminDTO adminDTO) {
        // Username veya email benzersizlik kontrolü
        if (adminRepository.existsByUsername(adminDTO.getUsername())) {
            throw new IllegalArgumentException("Bu username zaten var");
        }
        if (adminRepository.existsByEmail(adminDTO.getEmail())) {
            throw new IllegalArgumentException("Bu email zaten var");
        }
        
        Admin admin = new Admin();
        admin.setUsername(adminDTO.getUsername());
        admin.setPassword(adminDTO.getPassword()); // Gerçek uygulamada hashlenmeli (BCrypt)
        admin.setEmail(adminDTO.getEmail());
        admin.setFullName(adminDTO.getFullName());
        admin.setIsActive(true);
        admin.setIsSuperAdmin(adminDTO.getIsSuperAdmin() != null ? adminDTO.getIsSuperAdmin() : false);
        
        Admin savedAdmin = adminRepository.save(admin);
        return convertToDTO(savedAdmin);
    }
    
    @Override
    public AdminDTO getAdminById(Long id) {
        Admin admin = adminRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Admin bulunamadı"));
        return convertToDTO(admin);
    }
    
    @Override
    public AdminDTO getAdminByUsername(String username) {
        Admin admin = adminRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Admin bulunamadı"));
        return convertToDTO(admin);
    }
    
    @Override
    public AdminDTO getAdminByEmail(String email) {
        Admin admin = adminRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Admin bulunamadı"));
        return convertToDTO(admin);
    }
    
    @Override
    public List<AdminDTO> getAllAdmins() {
        return adminRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public AdminDTO updateAdmin(Long id, AdminDTO adminDTO) {
        Admin admin = adminRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Admin bulunamadı"));
        
        if (adminDTO.getFullName() != null) {
            admin.setFullName(adminDTO.getFullName());
        }
        if (adminDTO.getIsActive() != null) {
            admin.setIsActive(adminDTO.getIsActive());
        }
        if (adminDTO.getNotes() != null) {
            admin.setNotes(adminDTO.getNotes());
        }
        
        admin.setUpdatedAt(LocalDateTime.now());
        Admin updatedAdmin = adminRepository.save(admin);
        return convertToDTO(updatedAdmin);
    }
    
    @Override
    public void deleteAdmin(Long id) {
        if (!adminRepository.existsById(id)) {
            throw new ResourceNotFoundException("Admin bulunamadı");
        }
        adminRepository.deleteById(id);
    }
    
    @Override
    public void recordApproval(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
            .orElseThrow(() -> new ResourceNotFoundException("Admin bulunamadı"));
        admin.setApprovalsCount(admin.getApprovalsCount() + 1);
        admin.setUpdatedAt(LocalDateTime.now());
        adminRepository.save(admin);
    }
    
    @Override
    public void recordRejection(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
            .orElseThrow(() -> new ResourceNotFoundException("Admin bulunamadı"));
        admin.setRejectionsCount(admin.getRejectionsCount() + 1);
        admin.setUpdatedAt(LocalDateTime.now());
        adminRepository.save(admin);
    }
    
    @Override
    public void recordDeletion(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
            .orElseThrow(() -> new ResourceNotFoundException("Admin bulunamadı"));
        admin.setDeletionsCount(admin.getDeletionsCount() + 1);
        admin.setUpdatedAt(LocalDateTime.now());
        adminRepository.save(admin);
    }
    
    @Override
    public void updateLastLogin(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
            .orElseThrow(() -> new ResourceNotFoundException("Admin bulunamadı"));
        admin.setLastLoginAt(LocalDateTime.now());
        adminRepository.save(admin);
    }
    
    @Override
    public boolean verifyAdminExists(Long adminId) {
        return adminRepository.existsById(adminId);
    }
    
    @Override
    public boolean isAdmin(Long adminId) {
        // Admin ID varsa admin kabul edilir
        return adminRepository.existsById(adminId);
    }
    
    private AdminDTO convertToDTO(Admin admin) {
        return new AdminDTO(
            admin.getId(),
            admin.getUsername(),
            null,  // password - DTO'da gösterilmez
            admin.getEmail(),
            admin.getFullName(),
            admin.getIsActive(),
            admin.getIsSuperAdmin(),
            admin.getApprovalsCount(),
            admin.getRejectionsCount(),
            admin.getDeletionsCount(),
            admin.getCreatedAt(),
            admin.getUpdatedAt(),
            admin.getLastLoginAt(),
            admin.getNotes()
        );
    }
}
