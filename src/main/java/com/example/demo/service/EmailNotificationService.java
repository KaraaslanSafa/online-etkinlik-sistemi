package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailNotificationService {

    // Gerçek uygulamada JavaMailSender enjekte edilebilir.
    // private final JavaMailSender javaMailSender;

    @Async
    public void sendEmail(String to, String subject, String body) {
        try {
            // Mock email sending
            log.info("Email Gönderiliyor: Kime: {}, Konu: {}", to, subject);
            log.debug("İçerik: {}", body);
            
            // Thread.sleep(1000); // Gerçek gönderimi simüle etmek için
            log.info("Email başarıyla gönderildi: {}", to);
        } catch (Exception e) {
            log.error("Email gönderiminde hata: {}", e.getMessage());
        }
    }
}
