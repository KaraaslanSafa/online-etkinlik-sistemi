package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * EventOrganizerDTO - Organizatör Bilgisi DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventOrganizerDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String bio;
    private Integer foundedYear;
    private Integer totalEvents;
    private Integer totalParticipants;
    private String website;
    private Double averageRating;
    private Integer reviewCount;
    private Boolean isVerified;
}
