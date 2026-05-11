package com.example.demo.config;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.entity.Admin;
import com.example.demo.entity.Category;
import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(CategoryRepository categoryRepository, AdminRepository adminRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.categoryRepository = categoryRepository;
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (categoryRepository.count() == 0) {
            log.info("Veritabanı boş, varsayılan kategoriler ekleniyor...");
            
            categoryRepository.save(new Category("Müzik & Konser", "Konserler, festivaller ve müzikal etkinlikler"));
            categoryRepository.save(new Category("Spor & Egzersiz", "Futbol, basketbol, maraton ve fitness etkinlikleri"));
            categoryRepository.save(new Category("Tiyatro & Sanat", "Tiyatro oyunları, sergiler ve sanatsal gösteriler"));
            categoryRepository.save(new Category("Eğitim & Seminer", "Akademik dersler, profesyonel eğitimler ve seminerler"));
            categoryRepository.save(new Category("Teknoloji & Yazılım", "Hackathonlar, teknoloji konferansları ve sunumlar"));

            log.info("Varsayılan kategoriler başarıyla yüklendi.");
        }

        // Seeding to the general users table for Security Login
        if (userRepository.count() == 0) {
            log.info("Varsayılan kullanıcı listesi boş, admin kullanıcısı oluşturuluyor...");
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setEmail("admin@example.com");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setFirstName("Sistem");
            adminUser.setLastName("Yöneticisi");
            adminUser.setPhoneNumber("05555555555");
            adminUser.setUserRole(UserRole.ADMIN);
            adminUser.setIsActive(true);
            adminUser.setIsEmailVerified(true);
            adminUser.setIsPhoneVerified(true);
            adminUser.setCreatedAt(LocalDateTime.now());
            
            userRepository.save(adminUser);
            log.info("Varsayılan admin kullanıcısı Users tablosuna başarıyla kaydedildi! Kullanıcı adı: admin, Şifre: admin123");
        }

        if (adminRepository.count() == 0) {
            log.info("Varsayılan admin kullanıcısı oluşturuluyor...");
            
            Admin admin = new Admin(
                null, 
                "admin", 
                passwordEncoder.encode("admin123"), 
                "admin@example.com", 
                "Sistem Yöneticisi", 
                true, 
                true, 
                0, 
                0, 
                0, 
                LocalDateTime.now(), 
                null, 
                null, 
                "Sistem tarafından oluşturulan varsayılan admin"
            );
            
            adminRepository.save(admin);
            log.info("Varsayılan admin başarıyla oluşturuldu. Kullanıcı adı: admin, Şifre: admin123");
        }
    }
}
