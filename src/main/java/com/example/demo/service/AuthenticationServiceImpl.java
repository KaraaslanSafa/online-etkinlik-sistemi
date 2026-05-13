package com.example.demo.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtTokenProvider;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            User user = (User) authentication.getPrincipal();
            
            // Son giriş zamanını güncelle
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
            
            String accessToken = jwtTokenProvider.generateToken(user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user);
            
            UserDTO userDTO = convertToDTO(user);
            
            log.info("Kullanıcı başarıyla giriş yaptı: {}", loginRequest.getUsername());
            
            return new LoginResponse(accessToken, refreshToken, 86400000L, userDTO);
        } catch (RuntimeException ex) {
            // Yukarıda fırlattığımız özel hatayı (OTP hatası vb.) yakala ve aynen fırlat
            if (ex.getMessage().contains("doğrulayın")) {
                throw ex;
            }
            log.error("Giriş başarısız: {}", loginRequest.getUsername());
            throw new RuntimeException("Kullanıcı adı veya şifre hatalı", ex);
        } catch (Exception ex) {
            log.error("Giriş başarısız: {}", loginRequest.getUsername());
            throw new RuntimeException("Kullanıcı adı veya şifre hatalı", ex);
        }
    }
    
    @Override
    public UserDTO register(UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        log.info("Yeni kullanıcı kaydı oluşturuldu: {}", userDTO.getUsername());
        return createdUser;
    }
    
    @Override
    public LoginResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Geçersiz refresh token");
        }
        
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı"));
        
        String newAccessToken = jwtTokenProvider.generateToken(user);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);
        
        UserDTO userDTO = convertToDTO(user);
        
        return new LoginResponse(newAccessToken, newRefreshToken, 86400000L, userDTO);
    }
    
    @Override
    public void logout(Long userId) {
        // Token'ı geçersiz kıl veya blacklist'e ekle
        log.info("Kullanıcı çıkış yaptı: {}", userId);
    }
    
    @Override
    public UserDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            return convertToDTO(user);
        }
        throw new RuntimeException("Oturum açmış kullanıcı bulunamadı");
    }
    
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());
        dto.setBio(user.getBio());
        dto.setIsActive(user.getIsActive());
        dto.setIsEmailVerified(user.getIsEmailVerified());
        dto.setIsPhoneVerified(user.getIsPhoneVerified());
        dto.setUserRole(user.getUserRole());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setLastLoginAt(user.getLastLoginAt());
        return dto;
    }
}
