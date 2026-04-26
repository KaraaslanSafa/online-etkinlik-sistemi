package com.example.demo.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Admin - Sistem Yöneticileri
 * Etkinlik onayları, reddetme, silme işlemlerini yapabilirler
 */
@Entity
@Table(name = "Admins")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Admin adı boş olamaz")
    @Column(nullable = false, unique = true)
    private String username;
    
    @NotBlank(message = "Şifre boş olamaz")
    @Column(nullable = false)
    private String password; // Gerçek uygulamada hashlenmeli
    
    @NotBlank(message = "Email boş olamaz")
    @Email(message = "Geçersiz email formatı")
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String fullName;
    
    @Column(columnDefinition = "BIT DEFAULT 1")
    private Boolean isActive = true;
    
    @Column(columnDefinition = "BIT DEFAULT 0")
    private Boolean isSuperAdmin = false;
    
    // İstatistikler
    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer approvalsCount = 0;
    
    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer rejectionsCount = 0;
    
    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer deletionsCount = 0;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime updatedAt;
    
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime lastLoginAt;
    
    @Column(length = 500)
    private String notes;
}
