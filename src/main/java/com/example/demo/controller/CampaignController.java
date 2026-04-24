package com.example.demo.controller;

import com.example.demo.dto.CampaignDTO;
import com.example.demo.service.CampaignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/campaigns")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Fırsatlar ve Kampanyalar", description = "Müşteriler için aktif etkinlik fırsatları")
public class CampaignController {
    
    private final CampaignService campaignService;
    
    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }
    
    @GetMapping("/active")
    @Operation(summary = "Aktif Fırsatları Listele", description = "Müşterilerin ana ekranda görebileceği, süresi geçmemiş güncel indirimli etkinlikleri getirir")
    public ResponseEntity<List<CampaignDTO>> getActiveCampaigns() {
        List<CampaignDTO> activeCampaigns = campaignService.getActiveCampaigns();
        return ResponseEntity.ok(activeCampaigns);
    }
}