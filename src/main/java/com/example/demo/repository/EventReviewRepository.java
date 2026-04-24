package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.EventReview;

@Repository
public interface EventReviewRepository extends JpaRepository<EventReview, Long> {
    
    List<EventReview> findByEventId(Long eventId);
    List<EventReview> findByParticipantId(Long participantId);
    Optional<EventReview> findByEventIdAndParticipantId(Long eventId, Long participantId);
    boolean existsByEventIdAndParticipantId(Long eventId, Long participantId);
    
    @Query("SELECT AVG(CAST(e.rating AS DECIMAL(3,2))) FROM EventReview e WHERE e.event.id = :eventId")
    Double getAverageRatingByEventId(@Param("eventId") Long eventId);
    
    @Query("SELECT e FROM EventReview e WHERE e.event.id = :eventId AND e.rating >= :minRating ORDER BY e.createdAt DESC")
    List<EventReview> findByEventIdAndMinRating(@Param("eventId") Long eventId, @Param("minRating") Integer minRating);
    
    @Query("SELECT e FROM EventReview e WHERE e.event.id = :eventId ORDER BY e.helpfulCount DESC")
    List<EventReview> findMostHelpfulReviewsByEventId(@Param("eventId") Long eventId);
    
    @Query("SELECT COUNT(e) FROM EventReview e WHERE e.event.id = :eventId")
    Integer getReviewCountByEventId(@Param("eventId") Long eventId);
}
