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
    
    public boolean verifyOtp(String email, String otp) {
        String storedOtp = otpStorage.get(email);
        if (storedOtp != null && storedOtp.equals(otp)) {
            otpStorage.remove(email);
            
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı"));
            user.setIsEmailVerified(true);
            userRepository.save(user);
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
        
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setUserRole(userDTO.getUserRole() != null ? userDTO.getUserRole() : UserRole.USER);
        user.setIsActive(true);
        // OTP e-posta doğrulaması için false yapıldı:
        user.setIsEmailVerified(false);
        
        User savedUser = userRepository.save(user);
        
        // OTP Üret ve Gönder
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStorage.put(user.getEmail(), otp);
        emailService.sendOtpEmail(user.getEmail(), otp);
        
        log.info("Yeni kullanıcı oluşturuldu ve OTP gönderildi: {}", savedUser.getUsername());
        
        return convertToDTO(savedUser);
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
