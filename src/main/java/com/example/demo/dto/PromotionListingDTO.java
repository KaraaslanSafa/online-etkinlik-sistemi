package com.example.demo.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PromotionListingDTO - Vitrindeki Promosyon DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionListingDTO {
    private Long id;
    private Long campaignId;
    private String campaignTitle;
    private String campaignDescription;
    private String campaignType;
    private LocalDateTime featuredUntil;
    private Integer featuredPosition;
    private String featuredImageUrl;
    private String featuredDescription;
    private Integer viewCount;
    private Integer clickCount;
    private Integer conversionCount;
    private Integer displayPriority;
    private Boolean isCurrentlyFeatured;
    private Double clickThroughRate; // %
    private Double conversionRate;   // %
    
    // Campaign Detayı
    private Double discountPercentage;
    private Double discountAmount;
    private String campaignCode;
    
    // Event Detayı
    private Long eventId;
    private String eventTitle;
    private String eventCity;
    private LocalDateTime eventStartDate;
    private Double eventPrice;
    
    // Organizatör Detayı
    private Long organizerId;
    private String organizerName;
    private Double organizerRating;
}
