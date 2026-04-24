package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Campaign;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    
    List<Campaign> findByOrganizerId(Long organizerId);
    List<Campaign> findByEventId(Long eventId);
    Optional<Campaign> findByCampaignCode(String campaignCode);
    
    @Query("SELECT c FROM Campaign c WHERE c.isActive = true")
    List<Campaign> findAllActive();
    
    @Query("SELECT c FROM Campaign c WHERE c.isActive = true AND c.startDate <= :now AND c.endDate >= :now")
    List<Campaign> findCurrentlyActiveCampaigns(@Param("now") LocalDateTime now);
    
    @Query("SELECT c FROM Campaign c WHERE c.organizerId = :organizerId AND c.isActive = true")
    List<Campaign> findActiveByOrganizerId(@Param("organizerId") Long organizerId);
    
    @Query("SELECT c FROM Campaign c WHERE c.campaignType = :type AND c.isActive = true ORDER BY c.startDate DESC")
    List<Campaign> findByCampaignType(@Param("type") String campaignType);
    
    @Query("SELECT c FROM Campaign c WHERE c.eventId = :eventId AND c.isActive = true")
    List<Campaign> findActiveByEventId(@Param("eventId") Long eventId);
    
    @Query("SELECT c FROM Campaign c WHERE c.maxParticipants > c.usedCount AND c.isActive = true AND c.startDate <= :now AND c.endDate >= :now")
    List<Campaign> findAvailableCampaigns(@Param("now") LocalDateTime now);
}
