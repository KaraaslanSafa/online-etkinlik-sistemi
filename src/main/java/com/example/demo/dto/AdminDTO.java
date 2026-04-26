package com.example.demo.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AdminDTO - Admin veri aktarım nesnesi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDTO {
    
    private Long id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    
    @JsonProperty("isActive")
    private Boolean isActive;
    
    @JsonProperty("isSuperAdmin")
    private Boolean isSuperAdmin;
    
    private Integer approvalsCount;
    private Integer rejectionsCount;
    private Integer deletionsCount;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
    private String notes;
    
    // Getter ve Setter metodları
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
