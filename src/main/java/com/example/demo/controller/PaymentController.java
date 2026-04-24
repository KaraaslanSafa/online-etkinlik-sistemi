package com.example.demo.controller;

import com.example.demo.dto.PaymentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Ödeme ve Biletleme", description = "Bilet satın alma ve Mock Ödeme işlemleri")
public class PaymentController {

    @PostMapping("/process")
    @Operation(summary = "Sanal POS Ödeme İşlemi", description = "Mock ödeme alır ve başarılı olması durumunda bilet numarasını oluşturur")
    public ResponseEntity<String> processPayment(@RequestBody PaymentRequest request) {
        
        // Temel Validasyon
        if (request.getAmount() == null || request.getAmount() <= 0) {
            return ResponseEntity.badRequest().body("Geçersiz ödeme tutarı!");
        }
        if (request.getCardNumber() == null || request.getCardNumber().length() < 16) {
            return ResponseEntity.badRequest().body("Geçersiz kredi kartı numarası!");
        }

        // İlerleyen süreçte burada gerçek bir TicketService çağrılıp QR kodlu Ticket Entity'si kaydedilebilir.
        String transactionId = UUID.randomUUID().toString();
        return ResponseEntity.ok("Ödeme başarılı! Biletiniz tanımlandı. Referans No: TXN-" + transactionId);
    }
}