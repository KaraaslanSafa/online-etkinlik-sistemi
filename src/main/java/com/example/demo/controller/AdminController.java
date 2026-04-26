package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.AdminDTO;
import com.example.demo.service.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admins")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Yöneticiler", description = "Admin yönetimi API uç noktaları")
public class AdminController {
    
    private final AdminService adminService;
    
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    
    /**
     * Yeni admin oluştur
     */
    @PostMapping
    @Operation(summary = "Yeni admin oluştur", description = "Sistem tarafından yeni admin hesabı oluşturur")
    @ApiResponse(responseCode = "201", description = "Admin başarıyla oluşturuldu")
    @ApiResponse(responseCode = "400", description = "Geçersiz giriş")
    public ResponseEntity<AdminDTO> createAdmin(@Valid @RequestBody AdminDTO adminDTO) {
        AdminDTO createdAdmin = adminService.createAdmin(adminDTO);
        return new ResponseEntity<>(createdAdmin, HttpStatus.CREATED);
    }
    
    /**
     * Admin ID'si ile getir
     */
    @GetMapping("/{id}")
    @Operation(summary = "Adminı getir", description = "Belirtilen ID'ye sahip admin bilgilerini getirir")
    @ApiResponse(responseCode = "200", description = "Admin başarıyla getirildi")
    @ApiResponse(responseCode = "404", description = "Admin bulunamadı")
    public ResponseEntity<AdminDTO> getAdminById(@PathVariable Long id) {
        AdminDTO admin = adminService.getAdminById(id);
        return ResponseEntity.ok(admin);
    }
    
    /**
     * Username ile admin getir
     */
    @GetMapping("/username/{username}")
    @Operation(summary = "Admin'i username ile getir", description = "Belirtilen username'e sahip admin bilgilerini getirir")
    @ApiResponse(responseCode = "200", description = "Admin başarıyla getirildi")
    @ApiResponse(responseCode = "404", description = "Admin bulunamadı")
    public ResponseEntity<AdminDTO> getAdminByUsername(@PathVariable String username) {
        AdminDTO admin = adminService.getAdminByUsername(username);
        return ResponseEntity.ok(admin);
    }
    
    /**
     * Email ile admin getir
     */
    @GetMapping("/email/{email}")
    @Operation(summary = "Admin'i email ile getir", description = "Belirtilen email'e sahip admin bilgilerini getirir")
    @ApiResponse(responseCode = "200", description = "Admin başarıyla getirildi")
    @ApiResponse(responseCode = "404", description = "Admin bulunamadı")
    public ResponseEntity<AdminDTO> getAdminByEmail(@PathVariable String email) {
        AdminDTO admin = adminService.getAdminByEmail(email);
        return ResponseEntity.ok(admin);
    }
    
    /**
     * Tüm adminleri getir
     */
    @GetMapping
    @Operation(summary = "Tüm adminleri getir", description = "Sistemdeki tüm admin hesaplarını listeler")
    @ApiResponse(responseCode = "200", description = "Adminler başarıyla getirildi")
    public ResponseEntity<List<AdminDTO>> getAllAdmins() {
        List<AdminDTO> admins = adminService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }
    
    /**
     * Admin'i güncelle
     */
    @PutMapping("/{id}")
    @Operation(summary = "Admin'i güncelle", description = "Admin bilgilerini günceller")
    @ApiResponse(responseCode = "200", description = "Admin başarıyla güncellendi")
    @ApiResponse(responseCode = "404", description = "Admin bulunamadı")
    public ResponseEntity<AdminDTO> updateAdmin(@PathVariable Long id, 
                                                @Valid @RequestBody AdminDTO adminDTO) {
        AdminDTO updatedAdmin = adminService.updateAdmin(id, adminDTO);
        return ResponseEntity.ok(updatedAdmin);
    }
    
    /**
     * Admin'i sil
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Admin'i sil", description = "Admin hesabını sistemden siler")
    @ApiResponse(responseCode = "204", description = "Admin başarıyla silindi")
    @ApiResponse(responseCode = "404", description = "Admin bulunamadı")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }
}
