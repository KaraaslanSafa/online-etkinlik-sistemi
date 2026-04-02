package com.example.demo.repository;

import com.example.demo.entity.EventParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {
    List<EventParticipant> findByEventId(Long eventId);
    
    List<EventParticipant> findByParticipantId(Long participantId);
    
    Optional<EventParticipant> findByEventIdAndParticipantId(Long eventId, Long participantId);
    
    boolean existsByEventIdAndParticipantId(Long eventId, Long participantId);
    
    int countByEventId(Long eventId);
}
