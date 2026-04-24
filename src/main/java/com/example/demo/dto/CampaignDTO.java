package com.example.demo.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CampaignDTO - Kampanya ve Promosyon DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignDTO {
    private Long id;
    private Long organizerId;
    private String organizerName;
    private Long eventId;
    private String eventTitle;
    private String title;
    private String description;
    private Double discountPercentage;
    private Double discountAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer maxParticipants;
    private Integer usedCount;
    private String campaignCode;
    private Boolean isActive;
    private String campaignType; // DISCOUNT, EARLY_BIRD, SEASONAL, REFERRAL, BUNDLE
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isCurrentlyActive; // Runtime de hesaplanır
}
