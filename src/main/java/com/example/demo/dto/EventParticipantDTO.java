package com.example.demo.dto;

import com.example.demo.entity.ParticipationStatus;
import java.time.LocalDateTime;

public class EventParticipantDTO {
    private Long id;
    private Long eventId;
    private Long participantId;
    private String participantName;
    private String participantEmail;
    private ParticipationStatus status;
    private LocalDateTime registeredAt;
    
    public EventParticipantDTO() {
    }
    
    public EventParticipantDTO(Long id, Long eventId, Long participantId, String participantName, 
                              String participantEmail, ParticipationStatus status, LocalDateTime registeredAt) {
        this.id = id;
        this.eventId = eventId;
        this.participantId = participantId;
        this.participantName = participantName;
        this.participantEmail = participantEmail;
        this.status = status;
        this.registeredAt = registeredAt;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getEventId() {
        return eventId;
    }
    
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
    
    public Long getParticipantId() {
        return participantId;
    }
    
    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }
    
    public String getParticipantName() {
        return participantName;
    }
    
    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }
    
    public String getParticipantEmail() {
        return participantEmail;
    }
    
    public void setParticipantEmail(String participantEmail) {
        this.participantEmail = participantEmail;
    }
    
    public ParticipationStatus getStatus() {
        return status;
    }
    
    public void setStatus(ParticipationStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }
    
    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }
}
