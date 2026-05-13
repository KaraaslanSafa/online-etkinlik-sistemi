package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Value("${resend.api.key}")
    private String resendApiKey;
    
    @Value("${resend.from.email:onboarding@resend.dev}")
    private String fromEmail;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String RESEND_API_URL = "https://api.resend.com/emails";

    private void sendEmailViaResend(String to, String subject, String content) {
        try {
            if (resendApiKey == null || resendApiKey.isEmpty() || resendApiKey.contains("YOUR_API_KEY")) {
                logger.warn("Resend API Key yapılandırılmamış. E-posta gönderimi atlanıyor.");
                return;
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(resendApiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("from", fromEmail);
            body.put("to", to);
            body.put("subject", subject);
            body.put("html", content.replace("\n", "<br>"));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(RESEND_API_URL, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("E-posta başarıyla gönderildi (Resend API): " + to);
            } else {
                logger.error("Resend API hatası: " + response.getBody());
            }
        } catch (Exception e) {
            logger.error("E-posta gönderiminde hata oluştu (Resend): " + to, e);
        }
    }

    public void sendEventRegistrationConfirmation(String participantEmail, String participantName, 
                                                   String eventTitle, String eventDate) {
        String content = "Sayın " + participantName + ",\n\n" +
               "Etkinlik kaydınız başarıyla onaylanmıştır.\n\n" +
               "Etkinlik Detayları:\n" +
               "- Etkinlik Adı: " + eventTitle + "\n" +
               "- Tarih / Saat: " + eventDate + "\n\n" +
               "Lütfen saatinde etkinliğe katılmayı unutmayınız.\n\n" +
               "Sorularınız için bizimle iletişime geçebilirsiniz.\n\n" +
               "Saygılarımızla,\n" +
               "Etkinlik Yönetim Sistemi";
        sendEmailViaResend(participantEmail, "Etkinlik Kaydınız Onaylandı - " + eventTitle, content);
    }
    
    public void sendEventCancellationNotice(String participantEmail, String participantName, String eventTitle) {
        String content = "Sayın " + participantName + ",\n\n" +
               "Ne yazık ki '" + eventTitle + "' etkinliği iptal edilmiştir.\n" +
               "Kaydınız otomatik olarak silinmiştir.\n\n" +
               "Sizden özür dileriz.\n\n" +
               "Saygılarımızla,\n" +
               "Etkinlik Yönetim Sistemi";
        sendEmailViaResend(participantEmail, "Etkinlik İptal Bildirim - " + eventTitle, content);
    }
    
    public void sendEventReminder(String participantEmail, String participantName, 
                                   String eventTitle, String eventDate, String eventLocation) {
        String content = "Sayın " + participantName + ",\n\n" +
               "Kısa süre içinde kaydolduğunuz '" + eventTitle + "' etkinliği başlayacak!\n\n" +
               "Etkinlik Detayları:\n" +
               "- Etkinlik Adı: " + eventTitle + "\n" +
               "- Tarih / Saat: " + eventDate + "\n" +
               "- Lokasyon: " + eventLocation + "\n\n" +
               "Lütfen zamanında etkinliğe katılmayı unutmayınız.\n\n" +
               "Saygılarımızla,\n" +
               "Etkinlik Yönetim Sistemi";
        sendEmailViaResend(participantEmail, "Hatırlatıcı: " + eventTitle + " başlıyor!", content);
    }

    public void sendOtpEmail(String email, String otpCode) {
        logger.info("\n===============================================\n" +
                    "📧 SENDING REAL EMAIL VIA RESEND API\n" +
                    "TO: " + email + "\n" +
                    "SUBJECT: E-Posta Doğrulama Kodunuz\n" +
                    "CODE: " + otpCode + "\n" +
                    "===============================================\n");
        
        String content = "<h3>Etkinlik Yönetim Sistemine hoş geldiniz.</h3>" +
                         "<p>Kayıt işleminizi tamamlamak için doğrulama kodunuz:</p>" +
                         "<h2 style='color: #4a90e2;'>" + otpCode + "</h2>" +
                         "<p>Saygılarımızla,<br>Etkinlik Yönetim Sistemi</p>";
        
        sendEmailViaResend(email, "E-Posta Doğrulama Kodunuz", content);
    }

    public void sendEventApprovalNotice(String organizerEmail, String organizerName, String eventTitle) {
        String content = "Sayın " + organizerName + ",\n\n" +
                        "Tebrikler! '" + eventTitle + "' adlı etkinliğiniz admin tarafından onaylanmış ve yayına alınmıştır.\n\n" +
                        "Müşteriler artık bilet satın alabilirler.\n\n" +
                        "Saygılarımızla,\nEtkinlik Yönetim Sistemi";
        sendEmailViaResend(organizerEmail, "Etkinliğiniz Onaylandı - " + eventTitle, content);
    }

    public void sendEventRejectionNotice(String organizerEmail, String organizerName, String eventTitle, String reason) {
        String content = "Sayın " + organizerName + ",\n\n" +
                        "Maalesef '" + eventTitle + "' adlı etkinliğiniz admin tarafından reddedilmiştir.\n\n" +
                        "Red Sebebi: " + reason + "\n\n" +
                        "Lütfen gerekli düzenlemeleri yaparak tekrar onayabaşvurun.\n\n" +
                        "Saygılarımızla,\nEtkinlik Yönetim Sistemi";
        sendEmailViaResend(organizerEmail, "Etkinliğiniz Reddedildi - " + eventTitle, content);
    }
}
