package com.example.demo.controller;

import com.example.demo.dto.EventParticipantDTO;
import com.example.demo.entity.ParticipationStatus;
import com.example.demo.service.EventParticipantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/event-participants")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Etkinlik Katılımcıları", description = "Etkinlik katılımcı yönetimi API uç noktaları")
public class EventParticipantController {
    
    private final EventParticipantService eventParticipantService;
    
    public EventParticipantController(EventParticipantService eventParticipantService) {
        this.eventParticipantService = eventParticipantService;
    }
    
    @PostMapping("/register")
    @Operation(summary = "Etkinliğe katılımcı kaydet", 
               description = "Bir katılımcıyı etkinliğe kaydeder. Zaman çakışması ve kontenjan kontrolleri yapılır.")
    @ApiResponse(responseCode = "201", description = "Kayıt başarıyla yapıldı")
    @ApiResponse(responseCode = "409", description = "Zaman çakışması veya etkinlik dolu")
    @ApiResponse(responseCode = "404", description = "Etkinlik veya katılımcı bulunamadı")
    public ResponseEntity<EventParticipantDTO> registerParticipant(
            @RequestParam Long eventId,
            @RequestParam Long participantId) {
        EventParticipantDTO registration = eventParticipantService.registerParticipant(eventId, participantId);
        return new ResponseEntity<>(registration, HttpStatus.CREATED);
    }
    
    @DeleteMapping("/unregister")
    @Operation(summary = "Katılımcının etkinlik kaydını sil", description = "Bir katılımcını etkinlikten çıkarır")
    @ApiResponse(responseCode = "204", description = "Kayıt başarıyla silindi")
    @ApiResponse(responseCode = "404", description = "Kayıt bulunamadı")
    public ResponseEntity<Void> unregisterParticipant(
            @RequestParam Long eventId,
            @RequestParam Long participantId) {
        eventParticipantService.unregisterParticipant(eventId, participantId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/event/{eventId}")
    @Operation(summary = "Etkinliğin katılımcılarını getir", description = "Belirtilen etkinliğin tüm katılımcılarını listeler")
    @ApiResponse(responseCode = "200", description = "Katılımcılar başarıyla alındı")
    @ApiResponse(responseCode = "404", description = "Etkinlik bulunamadı")
    public ResponseEntity<List<EventParticipantDTO>> getEventParticipants(@PathVariable Long eventId) {
        List<EventParticipantDTO> participants = eventParticipantService.getEventParticipants(eventId);
        return ResponseEntity.ok(participants);
    }
    
    @GetMapping("/participant/{participantId}")
    @Operation(summary = "Katılımcının etkinliklerini getir", description = "Belirtilen katılımcının kayıtlı olduğu tüm etkinlikleri listeler")
    @ApiResponse(responseCode = "200", description = "Etkinlikler başarıyla alındı")
    @ApiResponse(responseCode = "404", description = "Katılımcı bulunamadı")
    public ResponseEntity<List<EventParticipantDTO>> getParticipantEvents(@PathVariable Long participantId) {
        List<EventParticipantDTO> events = eventParticipantService.getParticipantEvents(participantId);
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/event/{eventId}/count")
    @Operation(summary = "Etkinliğin katılımcı sayısını getir", description = "Belirtilen etkinliğe kayıtlı katılımcı sayısını getirir")
    @ApiResponse(responseCode = "200", description = "Sayı başarıyla alındı")
    @ApiResponse(responseCode = "404", description = "Etkinlik bulunamadı")
    public ResponseEntity<Integer> getEventParticipantCount(@PathVariable Long eventId) {
        int count = eventParticipantService.getEventParticipantCount(eventId);
        return ResponseEntity.ok(count);
    }
    
    @PatchMapping("/{eventParticipantId}/status")
    @Operation(summary = "Katılım durumunu güncelle", description = "Katılımcının katılım durumunu günceller (CONFIRMED, CANCELLED, etc.)")
    @ApiResponse(responseCode = "200", description = "Durum başarıyla güncellendi")
    @ApiResponse(responseCode = "404", description = "Kayıt bulunamadı")
    public ResponseEntity<Void> updateParticipationStatus(
            @PathVariable Long eventParticipantId,
            @RequestParam ParticipationStatus status) {
        eventParticipantService.updateParticipationStatus(eventParticipantId, status);
        return ResponseEntity.ok().build();
    }
}
