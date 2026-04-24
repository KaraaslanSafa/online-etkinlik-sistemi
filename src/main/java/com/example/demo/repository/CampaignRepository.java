package com.example.demo.repository;

import com.example.demo.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    List<Campaign> findByIsActiveTrueAndValidUntilAfter(LocalDateTime currentDate);
}