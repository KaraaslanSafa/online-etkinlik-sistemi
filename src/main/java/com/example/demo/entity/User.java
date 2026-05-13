package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User - Sistem Kullanıcıları (Katılımcı/Organizer)
 */
@Entity
@Table(name = "Users", indexes = {
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_username", columnList = "username")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Kullanıcı adı boş olamaz")
    @Column(nullable = false, unique = true, length = 100)
    private String username;
    
    @NotBlank(message = "Email boş olamaz")
    @Email(message = "Geçerli email adresi girin")
    @Column(nullable = false, unique = true, length = 150)
    private String email;
    
    @NotBlank(message = "Şifre boş olamaz")
    @Column(nullable = false, length = 255)
    private String password;
    
    @Column(length = 100)
    private String firstName;
    
    @Column(length = 100)
    private String lastName;
    
    @Column(length = 20)
    private String phoneNumber;
    
    @Column(length = 500)
    private String profilePictureUrl;
    
    @Column(length = 1000)
    private String bio;
    
    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;
    
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isEmailVerified = false;
    
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isPhoneVerified = false;
    
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(50) DEFAULT 'USER'")
    private UserRole userRole = UserRole.USER;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;
    
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime lastLoginAt;
    
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime deletedAt;
    
    @Column(columnDefinition = "VARCHAR(500)")
    private String deletionReason;
    
    // Spring Security alanları
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
    
    @Override
    public Set<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (userRole != null) {
            authorities.add(() -> "ROLE_" + userRole.name());
        }
        for (Role role : roles) {
            authorities.add(() -> "ROLE_" + role.getName());
            for (Permission permission : role.getPermissions()) {
                authorities.add(() -> permission.getName());
            }
        }
        return authorities;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return isActive;
    }
    
    @Override
    public String getUsername() {
        return username;
    }
    
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return firstName != null ? firstName : lastName;
    }
}
