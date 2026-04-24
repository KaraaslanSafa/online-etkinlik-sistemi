package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.OrganizerRating;

@Repository
public interface OrganizerRatingRepository extends JpaRepository<OrganizerRating, Long> {
    
    List<OrganizerRating> findByOrganizerId(Long organizerId);
    List<OrganizerRating> findByParticipantId(Long participantId);
    Optional<OrganizerRating> findByOrganizerIdAndParticipantId(Long organizerId, Long participantId);
    boolean existsByOrganizerIdAndParticipantId(Long organizerId, Long participantId);
    
    @Query("SELECT AVG(CAST(o.rating AS DECIMAL(3,2))) FROM OrganizerRating o WHERE o.organizer.id = :organizerId")
    Double getAverageRatingByOrganizerId(@Param("organizerId") Long organizerId);
    
    @Query("SELECT COUNT(o) FROM OrganizerRating o WHERE o.organizer.id = :organizerId")
    Integer getReviewCountByOrganizerId(@Param("organizerId") Long organizerId);
    
    @Query("SELECT o FROM OrganizerRating o WHERE o.organizer.id = :organizerId AND o.rating >= :minRating ORDER BY o.createdAt DESC")
    List<OrganizerRating> findByOrganizerIdAndMinRating(@Param("organizerId") Long organizerId, @Param("minRating") Integer minRating);
}
