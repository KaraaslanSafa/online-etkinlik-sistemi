package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Campaign - Organizatör Kampanyaları ve Promosyonları
 */
@Entity
@Table(name = "Campaigns")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Campaign {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organizer_id", nullable = false)
    private EventOrganizer organizer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event; // Null ise genel kampanya
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 2000)
    private String description;
    
    private Double discountPercentage; // 0-100
    private Double discountAmount;     // Sabit indirim tutarı
    
    @Column(nullable = false)
    private LocalDateTime startDate;
    
    @Column(nullable = false)
    private LocalDateTime endDate;
    
    private Integer maxParticipants;
    private Integer usedCount = 0;
    
    @Column(unique = true)
    private String campaignCode; // Promosyon kodu
    
    private Boolean isActive = true;
    
    // DISCOUNT, EARLY_BIRD, SEASONAL, REFERRAL, BUNDLE
    private String campaignType;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PromotionListing> promotionListings = new HashSet<>();
    
    /**
     * Kampanyanın halen aktif olup olmadığını kontrol et
     */
    public boolean isCurrentlyActive() {
        LocalDateTime now = LocalDateTime.now();
        return isActive && startDate.isBefore(now) && endDate.isAfter(now);
    }
    
    /**
     * Kampanyanın kullanılabilir olup olmadığını kontrol et
     */
    public boolean isUsageAvailable() {
        if (maxParticipants == null) {
            return true;
        }
        return usedCount < maxParticipants;
    }
}
