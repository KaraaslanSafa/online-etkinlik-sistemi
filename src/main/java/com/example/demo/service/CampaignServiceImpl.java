package com.example.demo.service;

import com.example.demo.dto.CampaignDTO;
import com.example.demo.entity.Campaign;
import com.example.demo.entity.Event;
import com.example.demo.repository.CampaignRepository;
import com.example.demo.repository.EventRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CampaignServiceImpl implements CampaignService {

    private final CampaignRepository campaignRepository;
    private final EventRepository eventRepository;

    public CampaignServiceImpl(CampaignRepository campaignRepository, EventRepository eventRepository) {
        this.campaignRepository = campaignRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public List<CampaignDTO> getActiveCampaigns() {
        return campaignRepository.findByIsActiveTrueAndValidUntilAfter(LocalDateTime.now())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CampaignDTO createCampaign(CampaignDTO dto) {
        // Burada yetkilendirme işlemleri ileride eklenebilir (Sadece ORGANIZER)
        return null; // Şimdilik sadece okuma işlemini aktifleştiriyoruz
    }

    private CampaignDTO convertToDTO(Campaign campaign) {
        CampaignDTO dto = new CampaignDTO();
        dto.setId(campaign.getId());
        if (campaign.getEvent() != null) {
            dto.setEventId(campaign.getEvent().getId());
        }
        dto.setTitle(campaign.getTitle());
        dto.setDescription(campaign.getDescription());
        dto.setDiscountPercentage(campaign.getDiscountPercentage());
        dto.setValidFrom(campaign.getValidFrom());
        dto.setValidUntil(campaign.getValidUntil());
        dto.setIsActive(campaign.getIsActive());
        return dto;
    }
}