package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.UserDTO;
import com.example.demo.service.AuthenticationService;
import com.example.demo.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * AuthenticationController - Kimlik Doğrulama Endpoint'leri
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Kimlik Doğrulama", description = "Giriş, kayıt ve token yönetimi")
public class AuthenticationController {
    
    @Autowired
    private AuthenticationService authenticationService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    @Operation(summary = "Kullanıcı Giriş", description = "Kullanıcı adı ve şifre ile giriş yapın")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authenticationService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    @Operation(summary = "Kullanıcı Kaydı", description = "Yeni kullanıcı hesabı oluşturun")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody UserDTO userDTO) {
        UserDTO createdUser = authenticationService.register(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    
    @PostMapping("/refresh-token")
    @Operation(summary = "Token Yenileme", description = "Refresh token kullanarak yeni access token alın")
    public ResponseEntity<LoginResponse> refreshToken(
            @RequestHeader(value = "Authorization", required = true) String bearerToken) {
        String refreshToken = bearerToken.startsWith("Bearer ") ? 
            bearerToken.substring(7) : bearerToken;
        LoginResponse response = authenticationService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    @Operation(summary = "Çıkış", description = "Oturum kapat")
    public ResponseEntity<String> logout() {
        UserDTO currentUser = authenticationService.getCurrentUser();
        authenticationService.logout(currentUser.getId());
        return ResponseEntity.ok("Başarıyla çıkış yapıldı");
    }
    
    @GetMapping("/me")
    @Operation(summary = "Mevcut Kullanıcı", description = "Oturum açan kullanıcının bilgilerini al")
    public ResponseEntity<UserDTO> getCurrentUser() {
        UserDTO currentUser = authenticationService.getCurrentUser();
        return ResponseEntity.ok(currentUser);
    }
    
    @PutMapping("/me")
    @Operation(summary = "Profili Güncelle", description = "Mevcut kullanıcının profilini güncelle")
    public ResponseEntity<UserDTO> updateProfile(@Valid @RequestBody UserDTO userDTO) {
        UserDTO currentUser = authenticationService.getCurrentUser();
        UserDTO updatedUser = userService.updateUser(currentUser.getId(), userDTO);
        return ResponseEntity.ok(updatedUser);
    }
    
    @PostMapping("/verify-email")
    @Operation(summary = "Email Doğrulama", description = "Email adresini doğrula")
    public ResponseEntity<String> verifyEmail(@RequestHeader(value = "Authorization") String bearerToken) {
        UserDTO currentUser = authenticationService.getCurrentUser();
        userService.verifyEmail(currentUser.getId());
        return ResponseEntity.ok("Email başarıyla doğrulandı");
    }
    
    @PostMapping("/verify-otp")
    @Operation(summary = "OTP ile Email Doğrulama", description = "Kayıt sırasında gönderilen OTP kodunu doğrula")
    public ResponseEntity<?> verifyOtp(@RequestBody java.util.Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        
        if (email == null || otp == null) {
            return ResponseEntity.badRequest().body("Email ve OTP kodu gereklidir.");
        }
        
        boolean verified = userService.verifyOtp(email, otp);
        if (verified) {
            return ResponseEntity.ok("E-posta başarıyla doğrulandı.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Geçersiz veya süresi dolmuş doğrulama kodu.");
        }
    }
    
    @PostMapping("/verify-phone")
    @Operation(summary = "Telefon Doğrulama", description = "Telefon numarasını doğrula")
    public ResponseEntity<String> verifyPhone(@RequestHeader(value = "Authorization") String bearerToken) {
        UserDTO currentUser = authenticationService.getCurrentUser();
        userService.verifyPhone(currentUser.getId());
        return ResponseEntity.ok("Telefon başarıyla doğrulandı");
    }
    
    @PostMapping("/change-password")
    @Operation(summary = "Şifre Değiştir", description = "Kullanıcı şifresini değiştir")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {
        // TODO: Şifre değiştirme implementasyonu
        return ResponseEntity.ok("Şifre başarıyla değiştirildi");
    }
}

/**
 * ChangePasswordRequest - Şifre Değiştirme İsteği
 */
class ChangePasswordRequest {
    public String oldPassword;
    public String newPassword;
    public String confirmPassword;
}
