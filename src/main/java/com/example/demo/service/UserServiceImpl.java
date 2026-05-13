package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Random;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;
    
    // Geçici OTP deposu: email -> otp_code
    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();
    
    // Geçici Kullanıcı deposu: email -> UserDTO
    private final Map<String, UserDTO> pendingRegistrations = new ConcurrentHashMap<>();
    
    // Geçici Şifre Sıfırlama deposu: email -> otp_code
    private final Map<String, String> resetOtpStorage = new ConcurrentHashMap<>();
    
    @Override
    public String forgotPassword(String email) {
        userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Bu e-posta adresine ait kullanıcı bulunamadı."));
            
        // OTP Üret ve Gönder
        String otp = String.format("%06d", new Random().nextInt(999999));
        resetOtpStorage.put(email, otp);
        emailService.sendOtpEmail(email, otp); // Email gönderiyoruz
        
        log.info("Şifre sıfırlama için OTP üretildi: {}", email);
        return otp; // Dev ortamında frontend'e göstermek için geri dönüyoruz
    }
    
    @Override
    public void resetPassword(String email, String otp, String newPassword) {
        String storedOtp = resetOtpStorage.get(email);
        if (storedOtp != null && storedOtp.equals(otp)) {
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı"));
                
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            resetOtpStorage.remove(email);
            log.info("Kullanıcı şifresi başarıyla sıfırlandı: {}", email);
        } else {
            throw new IllegalArgumentException("Geçersiz veya süresi dolmuş doğrulama kodu.");
        }
    }
    
    public boolean verifyOtp(String email, String otp) {
        String storedOtp = otpStorage.get(email);
        if (storedOtp != null && storedOtp.equals(otp)) {
            otpStorage.remove(email);
            
            // Geçici hafızada bu maile ait kayıt var mı?
            UserDTO pendingUserDTO = pendingRegistrations.get(email);
            if (pendingUserDTO != null) {
                // Varsa artık kalıcı olarak veritabanına ekle
                User user = new User();
                user.setUsername(pendingUserDTO.getUsername());
                user.setEmail(pendingUserDTO.getEmail());
                user.setPassword(passwordEncoder.encode(pendingUserDTO.getPassword()));
                user.setFirstName(pendingUserDTO.getFirstName());
                user.setLastName(pendingUserDTO.getLastName());
                user.setPhoneNumber(pendingUserDTO.getPhoneNumber());
                user.setUserRole(pendingUserDTO.getUserRole() != null ? pendingUserDTO.getUserRole() : UserRole.USER);
                user.setIsActive(true);
                user.setIsEmailVerified(true);
                
                userRepository.save(user);
                pendingRegistrations.remove(email);
                log.info("Geçici kayıt doğrulandı ve kalıcı olarak eklendi: {}", email);
            } else {
                // Hafızada yoksa zaten veritabanındadır (Örn: şifremi unuttum / mail onaylama)
                userRepository.findByEmail(email).ifPresent(user -> {
                    user.setIsEmailVerified(true);
                    userRepository.save(user);
                });
            }
            return true;
        }
        return false;
    }
    
    @Override
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("Bu kullanıcı adı zaten mevcut");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Bu email zaten mevcut");
        }
        
        // Veritabanına kaydetmek yerine geçici RAM belleğe kaydediyoruz
        pendingRegistrations.put(userDTO.getEmail(), userDTO);
        
        // OTP Üret ve Gönder
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStorage.put(userDTO.getEmail(), otp);
        emailService.sendOtpEmail(userDTO.getEmail(), otp);
        
        log.info("Kullanıcı kaydı geçici hafızaya alındı ve OTP gönderildi: {}", userDTO.getUsername());
        
        // Geliştirme (Dev) ortamında Railway e-postaları engellediği için OTP'yi bio içinde frontend'e dönüyoruz
        userDTO.setBio("DEV_OTP:" + otp);
        
        // Şimdilik ID'siz UserDTO dönüyoruz
        return userDTO;
    }
    
    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + id));
        return convertToDTO(user);
    }
    
    @Override
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + username));
        return convertToDTO(user);
    }
    
    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Email bulunamadı: " + email));
        return convertToDTO(user);
    }
    
    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public Page<UserDTO> getAllUsersPaginated(Pageable pageable) {
        return userRepository.findAllActive(pageable)
            .map(this::convertToDTO);
    }
    
    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + id));
        
        if (userDTO.getFirstName() != null) {
            user.setFirstName(userDTO.getFirstName());
        }
        if (userDTO.getLastName() != null) {
            user.setLastName(userDTO.getLastName());
        }
        if (userDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }
        if (userDTO.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(userDTO.getProfilePictureUrl());
        }
        if (userDTO.getBio() != null) {
            user.setBio(userDTO.getBio());
        }
        if (userDTO.getIsActive() != null) {
            user.setIsActive(userDTO.getIsActive());
        }
        if (userDTO.getUserRole() != null) {
            user.setUserRole(userDTO.getUserRole());
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);
        log.info("Kullanıcı güncellendi: {}", id);
        
        return convertToDTO(updatedUser);
    }
    
    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Kullanıcı bulunamadı: " + id);
        }
        userRepository.deleteById(id);
        log.info("Kullanıcı silindi: {}", id);
    }
    
    @Override
    public void softDeleteUser(Long id, String reason) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + id));
        user.setIsActive(false);
        user.setDeletedAt(LocalDateTime.now());
        user.setDeletionReason(reason);
        userRepository.save(user);
        log.info("Kullanıcı soft delete edildi: {}", id);
    }
    
    @Override
    public void verifyEmail(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + userId));
        user.setIsEmailVerified(true);
        userRepository.save(user);
        log.info("Email doğrulandı: {}", userId);
    }
    
    @Override
    public void verifyPhone(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + userId));
        user.setIsPhoneVerified(true);
        userRepository.save(user);
        log.info("Telefon doğrulandı: {}", userId);
    }
    
    @Override
    public boolean isEmailVerified(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + userId));
        return user.getIsEmailVerified();
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));
        log.debug("Kullanıcı yüklendi: {}", username);
        return user;
    }
    
    @Override
    public boolean checkPasswordMatch(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    @Override
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
    
    @Override
    public void assignRole(Long userId, String roleName) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + userId));
        
        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new ResourceNotFoundException("Rol bulunamadı: " + roleName));
        
        user.getRoles().add(role);
        userRepository.save(user);
        log.info("Rol atandı - Kullanıcı: {}, Rol: {}", userId, roleName);
    }
    
    @Override
    public void removeRole(Long userId, String roleName) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + userId));
        
        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new ResourceNotFoundException("Rol bulunamadı: " + roleName));
        
        user.getRoles().remove(role);
        userRepository.save(user);
        log.info("Rol kaldırıldı - Kullanıcı: {}, Rol: {}", userId, roleName);
    }
    
    @Override
    public void updateLastLogin(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + userId));
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }
    
    @Override
    public List<UserDTO> getUsersByRole(UserRole role) {
        return userRepository.findByUserRole(role).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public Page<UserDTO> getUsersByRole(UserRole role, Pageable pageable) {
        return userRepository.findByUserRole(role, pageable)
            .map(this::convertToDTO);
    }
    
    @Override
    public Page<UserDTO> searchUsers(String name, Pageable pageable) {
        return userRepository.searchByName(name, pageable)
            .map(this::convertToDTO);
    }
    
    @Override
    public Optional<UserDTO> findByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email)
            .map(this::convertToDTO);
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
