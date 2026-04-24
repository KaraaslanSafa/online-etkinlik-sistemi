package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.dto.CampaignDTO;

public interface CampaignService {
    
    CampaignDTO createCampaign(Long organizerId, Long eventId, String title, String description, 
                               Double discountPercentage, Double discountAmount, 
                               LocalDateTime startDate, LocalDateTime endDate, 
                               Integer maxParticipants, String campaignCode, String campaignType);
    
    CampaignDTO getCampaign(Long id);
    CampaignDTO getByCampaignCode(String code);
    List<CampaignDTO> getCampaignsByOrganizer(Long organizerId);
    List<CampaignDTO> getCampaignsByEvent(Long eventId);
    List<CampaignDTO> getActiveCampaigns();
    List<CampaignDTO> getCurrentlyActiveCampaigns();
    List<CampaignDTO> getCampaignsByType(String campaignType);
    List<CampaignDTO> getAvailableCampaigns();
    CampaignDTO updateCampaign(Long id, String title, String description, Double discountPercentage);
    void deactivateCampaign(Long id);
    void incrementUsageCount(Long campaignId);
}
