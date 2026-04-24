package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * EventDetailedDTO - Etkinlik Detaylı Bilgisi (Organizatör ve Review'larla)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDetailedDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private String city;
    private Double price;
    private Boolean isFree;
    private Integer capacity;
    private Integer participantCount;
    private Boolean isAvailable;
    private String categoryName;
    private Long categoryId;
    private String status;
    private Double averageRating;
    private Integer reviewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Organizatör Bilgisi
    private EventOrganizerDTO organizer;
    
    // Değerlendirmeler
    private List<EventReviewDTO> reviews;
    
    // İstatistikler
    private Integer totalParticipantsOfOrganizer;
    private Integer totalEventsOfOrganizer;
}
