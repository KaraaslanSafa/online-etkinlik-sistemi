package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.PromotionListing;

@Repository
public interface PromotionListingRepository extends JpaRepository<PromotionListing, Long> {
    
    List<PromotionListing> findByCampaignId(Long campaignId);
    
    @Query("SELECT p FROM PromotionListing p WHERE p.featuredUntil > :now ORDER BY p.displayPriority DESC, p.featuredPosition ASC")
    List<PromotionListing> findCurrentlyFeaturedPromotions(@Param("now") LocalDateTime now);
    
    @Query("SELECT p FROM PromotionListing p WHERE p.featuredUntil > :now AND p.displayPriority >= 7 ORDER BY p.displayPriority DESC")
    List<PromotionListing> findTopFeaturedPromotions(@Param("now") LocalDateTime now);
    
    @Query("SELECT p FROM PromotionListing p WHERE p.campaign.event.city = :city AND p.featuredUntil > :now ORDER BY p.displayPriority DESC")
    List<PromotionListing> findByCity(@Param("city") String city, @Param("now") LocalDateTime now);
    
    @Query("SELECT p FROM PromotionListing p WHERE p.campaign.organizer.id = :organizerId AND p.featuredUntil > :now")
    List<PromotionListing> findActiveByOrganizerId(@Param("organizerId") Long organizerId, @Param("now") LocalDateTime now);
    
    @Query("SELECT p FROM PromotionListing p WHERE p.featuredUntil > :now ORDER BY p.viewCount DESC")
    List<PromotionListing> findMostViewedPromotions(@Param("now") LocalDateTime now);
}
