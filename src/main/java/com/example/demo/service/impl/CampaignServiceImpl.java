package com.example.demo.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.CampaignDTO;
import com.example.demo.entity.Campaign;
import com.example.demo.entity.Event;
import com.example.demo.entity.EventOrganizer;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CampaignRepository;
import com.example.demo.repository.EventOrganizerRepository;
import com.example.demo.repository.EventRepository;
import com.example.demo.service.CampaignService;

@Service
public class CampaignServiceImpl implements CampaignService {
    
    @Autowired
    private CampaignRepository campaignRepository;
    
    @Autowired
    private EventOrganizerRepository organizerRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Override
    @Transactional
    public CampaignDTO createCampaign(Long organizerId, Long eventId, String title, String description, 
                                       Double discountPercentage, Double discountAmount,
                                       LocalDateTime startDate, LocalDateTime endDate, 
                                       Integer maxParticipants, String campaignCode, String campaignType) {
        
        EventOrganizer organizer = organizerRepository.findById(organizerId)
            .orElseThrow(() -> new ResourceNotFoundException("Organizer not found"));
        
        Campaign campaign = new Campaign();
        campaign.setOrganizer(organizer);
        campaign.setTitle(title);
        campaign.setDescription(description);
        campaign.setDiscountPercentage(discountPercentage);
        campaign.setDiscountAmount(discountAmount);
        campaign.setStartDate(startDate);
        campaign.setEndDate(endDate);
        campaign.setMaxParticipants(maxParticipants);
        campaign.setCampaignCode(campaignCode);
        campaign.setCampaignType(campaignType);
        campaign.setIsActive(true);
        
        if (eventId != null) {
            Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
            campaign.setEvent(event);
        }
        
        Campaign saved = campaignRepository.save(campaign);
        return convertToDTO(saved);
    }
    
    @Override
    public CampaignDTO getCampaign(Long id) {
        Campaign campaign = campaignRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        return convertToDTO(campaign);
    }
    
    @Override
    public CampaignDTO getByCampaignCode(String code) {
        Campaign campaign = campaignRepository.findByCampaignCode(code)
            .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with code: " + code));
        return convertToDTO(campaign);
    }
    
    @Override
    public List<CampaignDTO> getCampaignsByOrganizer(Long organizerId) {
        return campaignRepository.findByOrganizerId(organizerId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<CampaignDTO> getCampaignsByEvent(Long eventId) {
        return campaignRepository.findByEventId(eventId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<CampaignDTO> getActiveCampaigns() {
        return campaignRepository.findAllActive()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<CampaignDTO> getCurrentlyActiveCampaigns() {
        return campaignRepository.findCurrentlyActiveCampaigns(LocalDateTime.now())
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<CampaignDTO> getCampaignsByType(String campaignType) {
        return campaignRepository.findByCampaignType(campaignType)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<CampaignDTO> getAvailableCampaigns() {
        return campaignRepository.findAvailableCampaigns(LocalDateTime.now())
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public CampaignDTO updateCampaign(Long id, String title, String description, Double discountPercentage) {
        Campaign campaign = campaignRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        
        campaign.setTitle(title);
        campaign.setDescription(description);
        campaign.setDiscountPercentage(discountPercentage);
        campaign.setUpdatedAt(LocalDateTime.now());
        
        Campaign updated = campaignRepository.save(campaign);
        return convertToDTO(updated);
    }
    
    @Override
    @Transactional
    public void deactivateCampaign(Long id) {
        Campaign campaign = campaignRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        campaign.setIsActive(false);
        campaign.setUpdatedAt(LocalDateTime.now());
        campaignRepository.save(campaign);
    }
    
    @Override
    @Transactional
    public void incrementUsageCount(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        campaign.setUsedCount(campaign.getUsedCount() + 1);
        campaignRepository.save(campaign);
    }
    
    private CampaignDTO convertToDTO(Campaign campaign) {
        CampaignDTO dto = new CampaignDTO();
        dto.setId(campaign.getId());
        dto.setOrganizerId(campaign.getOrganizer().getId());
        dto.setOrganizerName(campaign.getOrganizer().getName());
        if (campaign.getEvent() != null) {
            dto.setEventId(campaign.getEvent().getId());
            dto.setEventTitle(campaign.getEvent().getTitle());
        }
        dto.setTitle(campaign.getTitle());
        dto.setDescription(campaign.getDescription());
        dto.setDiscountPercentage(campaign.getDiscountPercentage());
        dto.setDiscountAmount(campaign.getDiscountAmount());
        dto.setStartDate(campaign.getStartDate());
        dto.setEndDate(campaign.getEndDate());
        dto.setMaxParticipants(campaign.getMaxParticipants());
        dto.setUsedCount(campaign.getUsedCount());
        dto.setCampaignCode(campaign.getCampaignCode());
        dto.setIsActive(campaign.getIsActive());
        dto.setCampaignType(campaign.getCampaignType());
        dto.setIsCurrentlyActive(campaign.isCurrentlyActive());
        dto.setCreatedAt(campaign.getCreatedAt());
        dto.setUpdatedAt(campaign.getUpdatedAt());
        return dto;
    }
}
