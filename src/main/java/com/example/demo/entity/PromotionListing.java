package com.example.demo.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PromotionListing - Vitrindeki Promosyon Gösterimi
 * Kampanyaların müşteri tarafından görüldüğü yer
 */
@Entity
@Table(name = "PromotionListings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionListing {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;
    
    // Ne kadar süre için feature edilecek
    private LocalDateTime featuredUntil;
    
    // Vitrinde hangi pozisyonda gösterilecek (1-10)
    private Integer featuredPosition;
    
    // Promosyon için custom görsel
    private String featuredImageUrl;
    
    // Vitrinde gösterilecek açıklama
    @Column(length = 1000)
    private String featuredDescription;
    
    // Analytics
    private Integer viewCount = 0;
    private Integer clickCount = 0;
    private Integer conversionCount = 0;
    
    // Gösterme önceliği (yüksek = daha üstte gösterilir)
    private Integer displayPriority = 5; // 1-10
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    /**
     * Promosyon halen vitrinde gösterilip gösterilmediğini kontrol et
     */
    public boolean isCurrentlyFeatured() {
        if (featuredUntil == null) {
            return false;
        }
        return featuredUntil.isAfter(LocalDateTime.now());
    }
    
    /**
     * Click-through rate hesapla
     */
    public double getClickThroughRate() {
        if (viewCount == 0) {
            return 0.0;
        }
        return (double) clickCount / viewCount * 100;
    }
    
    /**
     * Conversion rate hesapla
     */
    public double getConversionRate() {
        if (clickCount == 0) {
            return 0.0;
        }
        return (double) conversionCount / clickCount * 100;
    }
}
