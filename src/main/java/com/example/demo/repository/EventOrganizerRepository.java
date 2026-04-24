package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.EventOrganizer;

@Repository
public interface EventOrganizerRepository extends JpaRepository<EventOrganizer, Long> {
    
    Optional<EventOrganizer> findByEmail(String email);
    Optional<EventOrganizer> findByName(String name);
    
    @Query("SELECT e FROM EventOrganizer e WHERE e.isVerified = true")
    List<EventOrganizer> findAllVerified();
    
    @Query("SELECT e FROM EventOrganizer e WHERE e.averageRating >= :minRating ORDER BY e.averageRating DESC")
    List<EventOrganizer> findByMinRating(@Param("minRating") Double minRating);
    
    @Query("SELECT e FROM EventOrganizer e ORDER BY e.averageRating DESC")
    List<EventOrganizer> findTopRatedOrganizers();
    
    @Query("SELECT e FROM EventOrganizer e ORDER BY e.totalEvents DESC")
    List<EventOrganizer> findMostActiveOrganizers();
}
