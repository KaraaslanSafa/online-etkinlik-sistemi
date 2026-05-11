package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.CampaignDTO;
import com.example.demo.service.CampaignService;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.EventOrganizerRepository;
import com.example.demo.entity.User;
import com.example.demo.entity.EventOrganizer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/campaigns")
@Tag(name = "Kampanyalar & Promosyonlar", description = "Kampanya ve indirim kuponu yönetimi")
public class CampaignController {

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventOrganizerRepository eventOrganizerRepository;

    @PostMapping
    @Operation(summary = "Kampanya Oluştur", description = "Yeni bir indirim kuponu/kampanya tanımlar")
    public ResponseEntity<CampaignDTO> createCampaign(@RequestBody CampaignDTO dto) {
        Long mappedOrganizerId = dto.getOrganizerId();

        // Eğer kullanıcı ID'si gelmişse, onu EventOrganizer ID'sine dönüştür
        if (dto.getOrganizerId() != null) {
            User user = userRepository.findById(dto.getOrganizerId()).orElse(null);
            if (user != null) {
                EventOrganizer organizer = eventOrganizerRepository.findByEmail(user.getEmail()).orElse(null);
                if (organizer != null) {
                    mappedOrganizerId = organizer.getId();
                }
            }
        }

        CampaignDTO created = campaignService.createCampaign(
            mappedOrganizerId,
            dto.getEventId(),
            dto.getTitle(),
            dto.getDescription(),
            dto.getDiscountPercentage(),
            dto.getDiscountAmount() != null ? dto.getDiscountAmount() : 0.0,
            dto.getStartDate() != null ? dto.getStartDate() : LocalDateTime.now(),
            dto.getEndDate() != null ? dto.getEndDate() : LocalDateTime.now().plusMonths(1),
            dto.getMaxParticipants() != null ? dto.getMaxParticipants() : 100,
            dto.getCampaignCode(),
            dto.getCampaignType() != null ? dto.getCampaignType() : "DISCOUNT"
        );
        return ResponseEntity.ok(created);
    }

    @GetMapping("/organizer/{organizerId}")
    @Operation(summary = "Organizatör Kampanyaları", description = "Belirli bir organizatörün tüm kampanyalarını listeler")
    public ResponseEntity<List<CampaignDTO>> getCampaignsByOrganizer(@PathVariable Long organizerId) {
        Long mappedId = organizerId;
        User user = userRepository.findById(organizerId).orElse(null);
        if (user != null) {
            EventOrganizer organizer = eventOrganizerRepository.findByEmail(user.getEmail()).orElse(null);
            if (organizer != null) {
                mappedId = organizer.getId();
            }
        }
        List<CampaignDTO> campaigns = campaignService.getCampaignsByOrganizer(mappedId);
        return ResponseEntity.ok(campaigns);
    }

    @GetMapping("/active")
    @Operation(summary = "Aktif Kampanyalar", description = "Vitrindeki tüm aktif kampanyaları getirir")
    public ResponseEntity<List<CampaignDTO>> getActiveCampaigns() {
        List<CampaignDTO> campaigns = campaignService.getCurrentlyActiveCampaigns();
        return ResponseEntity.ok(campaigns);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Kupon Sorgula", description = "Kupon koduna göre kampanya detayını getirir")
    public ResponseEntity<CampaignDTO> getByCampaignCode(@PathVariable String code) {
        CampaignDTO campaign = campaignService.getByCampaignCode(code);
        return ResponseEntity.ok(campaign);
    }

    @PostMapping("/{id}/deactivate")
    @Operation(summary = "Kampanyayı Deaktive Et", description = "Aktif bir kampanyayı sonlandırır")
    public ResponseEntity<Void> deactivateCampaign(@PathVariable Long id) {
        campaignService.deactivateCampaign(id);
        return ResponseEntity.ok().build();
    }
}
