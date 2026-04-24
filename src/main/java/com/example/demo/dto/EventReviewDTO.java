package com.example.demo.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * EventReviewDTO - Etkinlik Değerlendirmesi DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventReviewDTO {
    private Long id;
    private Long eventId;
    private Long participantId;
    private String participantName; // Gizlilik için kısmi isim
    private Integer rating;
    private String title;
    private String comment;
    private Integer helpfulCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Request için
    private String eventTitle;
}
