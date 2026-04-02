package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "EventParticipants", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"event_id", "participant_id"})
})
public class EventParticipant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipationStatus status = ParticipationStatus.REGISTERED;
    
    @Column(nullable = false)
    private LocalDateTime registeredAt = LocalDateTime.now();
    
    public EventParticipant() {
    }
    
    public EventParticipant(Event event, Participant participant) {
        this.event = event;
        this.participant = participant;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Event getEvent() {
        return event;
    }
    
    public void setEvent(Event event) {
        this.event = event;
    }
    
    public Participant getParticipant() {
        return participant;
    }
    
    public void setParticipant(Participant participant) {
        this.participant = participant;
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
