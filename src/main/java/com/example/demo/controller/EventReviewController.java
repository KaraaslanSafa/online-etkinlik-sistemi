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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.EventReviewDTO;
import com.example.demo.service.EventReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/event-reviews")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Etkinlik Yorumları", description = "Etkinlik yorum ve değerlendirme yönetimi API uç noktaları")
public class EventReviewController {
    
    private final EventReviewService eventReviewService;
    
    public EventReviewController(EventReviewService eventReviewService) {
        this.eventReviewService = eventReviewService;
    }
    
    /**
     * Etkinlik için yeni yorum/değerlendirme oluştur
     * 
     * @param eventId Etkinlik ID
     * @param participantId Katılımcı ID
     * @param rating Puanlandırma (1-5)
     * @param title Başlık
     * @param comment Yorum
     * @return Oluşturulan yorum
     */
    @PostMapping
    @Operation(summary = "Etkinlik için yorum oluştur", 
               description = "Bir etkinliğe katılımcı tarafından yorum/değerlendirme eklenir (1-5 puan)")
    @ApiResponse(responseCode = "201", description = "Yorum başarıyla oluşturuldu")
    @ApiResponse(responseCode = "404", description = "Etkinlik veya katılımcı bulunamadı")
    @ApiResponse(responseCode = "400", description = "Geçersiz puan (1-5 arası olmalı)")
    public ResponseEntity<EventReviewDTO> createReview(
            @RequestParam Long eventId,
            @RequestParam Long participantId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String comment) {
        
        EventReviewDTO review = eventReviewService.createReview(
            eventId, participantId, rating, title, comment
        );
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }
    
    /**
     * Yorum ID'si ile yorum getir
     */
    @GetMapping("/{id}")
    @Operation(summary = "Yorumu getir", description = "Belirtilen ID'ye sahip yorum bilgilerini getirir")
    @ApiResponse(responseCode = "200", description = "Yorum başarıyla getirildi")
    @ApiResponse(responseCode = "404", description = "Yorum bulunamadı")
    public ResponseEntity<EventReviewDTO> getReview(@PathVariable Long id) {
        EventReviewDTO review = eventReviewService.getReview(id);
        return ResponseEntity.ok(review);
    }
    
    /**
     * Etkinliğe ait tüm yorumları getir
     */
    @GetMapping("/event/{eventId}")
    @Operation(summary = "Etkinliğin yorumlarını getir", 
               description = "Belirtilen etkinliğin tüm yorum ve değerlendirmelerini listeler")
    @ApiResponse(responseCode = "200", description = "Yorumlar başarıyla getirildi")
    @ApiResponse(responseCode = "404", description = "Etkinlik bulunamadı")
    public ResponseEntity<List<EventReviewDTO>> getReviewsByEvent(@PathVariable Long eventId) {
        List<EventReviewDTO> reviews = eventReviewService.getReviewsByEvent(eventId);
        return ResponseEntity.ok(reviews);
    }
    
    /**
     * Katılımcının yazdığı tüm yorumları getir
     */
    @GetMapping("/participant/{participantId}")
    @Operation(summary = "Katılımcının yorumlarını getir", 
               description = "Belirtilen katılımcının yazdığı tüm yorum ve değerlendirmeleri listeler")
    @ApiResponse(responseCode = "200", description = "Yorumlar başarıyla getirildi")
    @ApiResponse(responseCode = "404", description = "Katılımcı bulunamadı")
    public ResponseEntity<List<EventReviewDTO>> getReviewsByParticipant(@PathVariable Long participantId) {
        List<EventReviewDTO> reviews = eventReviewService.getReviewsByParticipant(participantId);
        return ResponseEntity.ok(reviews);
    }
    
    /**
     * Etkinliğe ait belirtilen puan ve üzeri yorumları getir
     */
    @GetMapping("/event/{eventId}/min-rating")
    @Operation(summary = "Etkinliğin minimum puan ile yorumlarını getir", 
               description = "Belirtilen etkinliğin en az belirtilen puanı alan yorumlarını listeler")
    @ApiResponse(responseCode = "200", description = "Yorumlar başarıyla getirildi")
    public ResponseEntity<List<EventReviewDTO>> getReviewsByEventAndMinRating(
            @PathVariable Long eventId,
            @RequestParam Integer minRating) {
        List<EventReviewDTO> reviews = eventReviewService.getReviewsByEventAndMinRating(eventId, minRating);
        return ResponseEntity.ok(reviews);
    }
    
    /**
     * Etkinliğin en faydalı yorumlarını getir
     */
    @GetMapping("/event/{eventId}/most-helpful")
    @Operation(summary = "Etkinliğin en faydalı yorumlarını getir", 
               description = "Belirtilen etkinliğin en çok beğenilen/faydalı yorumlarını listeler")
    @ApiResponse(responseCode = "200", description = "Yorumlar başarıyla getirildi")
    public ResponseEntity<List<EventReviewDTO>> getMostHelpfulReviews(@PathVariable Long eventId) {
        List<EventReviewDTO> reviews = eventReviewService.getMostHelpfulReviews(eventId);
        return ResponseEntity.ok(reviews);
    }
    
    /**
     * Etkinliğin ortalama puanını getir
     */
    @GetMapping("/event/{eventId}/average-rating")
    @Operation(summary = "Etkinliğin ortalama puanını getir", 
               description = "Belirtilen etkinliğin tüm yorumlarına göre ortalama puanını hesaplar")
    @ApiResponse(responseCode = "200", description = "Puan başarıyla hesaplandı")
    public ResponseEntity<Double> getEventAverageRating(@PathVariable Long eventId) {
        Double averageRating = eventReviewService.getEventAverageRating(eventId);
        return ResponseEntity.ok(averageRating);
    }
    
    /**
     * Yorumu güncelle (sadece yazarı güncelleyebilir)
     */
    @PutMapping("/{id}")
    @Operation(summary = "Yorumu güncelle", 
               description = "Yazan kullanıcı tarafından yorum/değerlendirme güncellenir")
    @ApiResponse(responseCode = "200", description = "Yorum başarıyla güncellendi")
    @ApiResponse(responseCode = "404", description = "Yorum bulunamadı")
    @ApiResponse(responseCode = "403", description = "Yalnızca yorum yazarı güncelleyebilir")
    public ResponseEntity<EventReviewDTO> updateReview(
            @PathVariable Long id,
            @RequestParam Integer rating,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String comment,
            @RequestParam Long participantId) {
        
        // Yorum yazarı kontrolü service'de yapılabilir
        EventReviewDTO updatedReview = eventReviewService.updateReview(id, rating, title, comment);
        return ResponseEntity.ok(updatedReview);
    }
    
    /**
     * Yorumu sil (sadece yazarı veya admin silebilir)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Yorumu sil", 
               description = "Yorum yazarı veya admin tarafından yorum silinir")
    @ApiResponse(responseCode = "204", description = "Yorum başarıyla silindi")
    @ApiResponse(responseCode = "404", description = "Yorum bulunamadı")
    @ApiResponse(responseCode = "403", description = "Yalnızca yorum yazarı veya admin silebilir")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id,
            @RequestParam Long participantId) {
        
        // Silin kontrolü service'de yapılabilir
        eventReviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Yorumu faydalı olarak işaretle
     */
    @PostMapping("/{id}/mark-helpful")
    @Operation(summary = "Yorumu faydalı olarak işaretle", 
               description = "Diğer kullanıcılar tarafından faydalı olarak işaretlenen yorumlar sayılır")
    @ApiResponse(responseCode = "204", description = "Başarıyla işaretlendi")
    @ApiResponse(responseCode = "404", description = "Yorum bulunamadı")
    public ResponseEntity<Void> markAsHelpful(@PathVariable Long id) {
        eventReviewService.markAsHelpful(id);
        return ResponseEntity.noContent().build();
    }
}
