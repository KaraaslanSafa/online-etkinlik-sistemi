package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    /**
     * Etkinlik kaydı onay e-postası gönderi
     */
    public void sendEventRegistrationConfirmation(String participantEmail, String participantName, 
                                                   String eventTitle, String eventDate) {
        try {
            if (mailSender == null) {
                logger.warn("Mail server yapılandırılmamış. E-posta gönderimi atlanıyor.");
                return;
            }
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(participantEmail);
            message.setSubject("Etkinlik Kaydınız Onaylandı - " + eventTitle);
            message.setText(buildRegistrationEmailBody(participantName, eventTitle, eventDate));
            message.setFrom("noreply@eventmanagement.com");
            
            mailSender.send(message);
            logger.info("Kayıt onay e-postası gönderildi: " + participantEmail);
            
        } catch (Exception e) {
            logger.error("E-posta gönderiminde hata oluştu: " + participantEmail, e);
        }
    }
    
    /**
     * Etkinlik iptal e-postası gönder
     */
    public void sendEventCancellationNotice(String participantEmail, String participantName, String eventTitle) {
        try {
            if (mailSender == null) {
                logger.warn("Mail server yapılandırılmamış. E-posta gönderimi atlanıyor.");
                return;
            }
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(participantEmail);
            message.setSubject("Etkinlik İptal Bildirim - " + eventTitle);
            message.setText(buildCancellationEmailBody(participantName, eventTitle));
            message.setFrom("noreply@eventmanagement.com");
            
            mailSender.send(message);
            logger.info("İptal bildirimi e-postası gönderildi: " + participantEmail);
            
        } catch (Exception e) {
            logger.error("E-posta gönderiminde hata oluştu: " + participantEmail, e);
        }
    }
    
    /**
     * Etkinlik hatırlatıcı e-postası gönder
     */
    public void sendEventReminder(String participantEmail, String participantName, 
                                  String eventTitle, String eventDate, String eventLocation) {
        try {
            if (mailSender == null) {
                logger.warn("Mail server yapılandırılmamış. E-posta gönderimi atlanıyor.");
                return;
            }
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(participantEmail);
            message.setSubject("Hatırlatıcı: " + eventTitle + " başlıyor!");
            message.setText(buildReminderEmailBody(participantName, eventTitle, eventDate, eventLocation));
            message.setFrom("noreply@eventmanagement.com");
            
            mailSender.send(message);
            logger.info("Hatırlatıcı e-postası gönderildi: " + participantEmail);
            
        } catch (Exception e) {
            logger.error("E-posta gönderiminde hata oluştu: " + participantEmail, e);
        }
    }
    
    private String buildRegistrationEmailBody(String participantName, String eventTitle, String eventDate) {
        return "Sayın " + participantName + ",\n\n" +
               "Etkinlik kaydınız başarıyla onaylanmıştır.\n\n" +
               "Etkinlik Detayları:\n" +
               "- Etkinlik Adı: " + eventTitle + "\n" +
               "- Tarih / Saat: " + eventDate + "\n\n" +
               "Lütfen saatinde etkinliğe katılmayı unutmayınız.\n\n" +
               "Sorularınız için bizimle iletişime geçebilirsiniz.\n\n" +
               "Saygılarımızla,\n" +
               "Etkinlik Yönetim Sistemi";
    }
    
    private String buildCancellationEmailBody(String participantName, String eventTitle) {
        return "Sayın " + participantName + ",\n\n" +
               "Ne yazık ki '" + eventTitle + "' etkinliği iptal edilmiştir.\n" +
               "Kaydınız otomatik olarak silinmiştir ve ücretiniz (varsa) iade edilecektir.\n\n" +
               "Sizden özür dileriz.\n\n" +
               "Saygılarımızla,\n" +
               "Etkinlik Yönetim Sistemi";
    }
    
    private String buildReminderEmailBody(String participantName, String eventTitle, 
                                         String eventDate, String eventLocation) {
        return "Sayın " + participantName + ",\n\n" +
               "Kısa süre içinde kaydolduğunuz '" + eventTitle + "' etkinliği başlayacak!\n\n" +
               "Etkinlik Detayları:\n" +
               "- Etkinlik Adı: " + eventTitle + "\n" +
               "- Tarih / Saat: " + eventDate + "\n" +
               "- Lokasyon: " + eventLocation + "\n\n" +
               "Lütfen zamanında etkinliğe katılmayı unutmayınız.\n\n" +
               "Saygılarımızla,\n" +
               "Etkinlik Yönetim Sistemi";
    }

    /**
     * E-posta doğrulama için OTP gönder
     */
    public void sendOtpEmail(String email, String otpCode) {
        logger.info("\n===============================================\n" +
                    "📧 SIMULATED EMAIL (OTP VERIFICATION)\n" +
                    "TO: " + email + "\n" +
                    "SUBJECT: E-Posta Doğrulama Kodunuz\n" +
                    "BODY: Kayıt işleminizi tamamlamak için doğrulama kodunuz: " + otpCode + "\n" +
                    "===============================================\n");
        
        try {
            if (mailSender == null) {
                return;
            }
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("E-Posta Doğrulama Kodunuz");
            message.setText("Etkinlik Yönetim Sistemine hoş geldiniz.\n\n" +
                            "Kayıt işleminizi tamamlamak için doğrulama kodunuz: " + otpCode + "\n\n" +
                            "Saygılarımızla,\nEtkinlik Yönetim Sistemi");
            message.setFrom("noreply@eventmanagement.com");
            mailSender.send(message);
        } catch (Exception e) {
            logger.error("OTP E-posta gönderiminde hata oluştu: " + email, e);
        }
    }

    /**
     * Organizatöre etkinlik onay e-postası gönder
     */
    public void sendEventApprovalNotice(String organizerEmail, String organizerName, String eventTitle) {
        logger.info("\n===============================================\n" +
                    "📧 SIMULATED EMAIL (EVENT APPROVED)\n" +
                    "TO: " + organizerEmail + "\n" +
                    "SUBJECT: Etkinliğiniz Onaylandı - " + eventTitle + "\n" +
                    "BODY: Tebrikler! Etkinliğiniz admin tarafından onaylanmış ve yayına alınmıştır.\n" +
                    "===============================================\n");
        
        try {
            if (mailSender == null) {
                return;
            }
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(organizerEmail);
            message.setSubject("Etkinliğiniz Onaylandı - " + eventTitle);
            message.setText("Sayın " + organizerName + ",\n\n" +
                            "Tebrikler! '" + eventTitle + "' adlı etkinliğiniz admin tarafından onaylanmış ve yayına alınmıştır.\n\n" +
                            "Müşteriler artık bilet satın alabilirler.\n\n" +
                            "Saygılarımızla,\nEtkinlik Yönetim Sistemi");
            message.setFrom("noreply@eventmanagement.com");
            mailSender.send(message);
        } catch (Exception e) {
            logger.error("Onay e-posta gönderiminde hata: " + organizerEmail, e);
        }
    }

    /**
     * Organizatöre etkinlik ret e-postası gönder
     */
    public void sendEventRejectionNotice(String organizerEmail, String organizerName, String eventTitle, String reason) {
        logger.info("\n===============================================\n" +
                    "📧 SIMULATED EMAIL (EVENT REJECTED)\n" +
                    "TO: " + organizerEmail + "\n" +
                    "SUBJECT: Etkinliğiniz Reddedildi - " + eventTitle + "\n" +
                    "BODY: Etkinliğiniz aşağıdaki sebeple reddedilmiştir: " + reason + "\n" +
                    "===============================================\n");
        
        try {
            if (mailSender == null) {
                return;
            }
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(organizerEmail);
            message.setSubject("Etkinliğiniz Reddedildi - " + eventTitle);
            message.setText("Sayın " + organizerName + ",\n\n" +
                            "Maalesef '" + eventTitle + "' adlı etkinliğiniz admin tarafından reddedilmiştir.\n\n" +
                            "Red Sebebi: " + reason + "\n\n" +
                            "Lütfen gerekli düzenlemeleri yaparak tekrar onayabaşvurun.\n\n" +
                            "Saygılarımızla,\nEtkinlik Yönetim Sistemi");
            message.setFrom("noreply@eventmanagement.com");
            mailSender.send(message);
        } catch (Exception e) {
            logger.error("Ret e-posta gönderiminde hata: " + organizerEmail, e);
        }
    }
}
