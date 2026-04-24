package com.example.demo.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OrganizerRatingDTO - Organizatör Kalitesi Değerlendirmesi DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizerRatingDTO {
    private Long id;
    private Long organizerId;
    private String organizerName;
    private Long participantId;
    private String participantName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
