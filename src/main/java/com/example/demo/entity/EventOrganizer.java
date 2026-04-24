package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * EventOrganizer - Etkinlik Düzenleyen Kurum/Kişi Bilgileri
 */
@Entity
@Table(name = "EventOrganizers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventOrganizer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String email;
    private String phone;
    
    @Column(length = 1000)
    private String bio;
    
    private Integer foundedYear;
    private Integer totalEvents = 0;
    private Integer totalParticipants = 0;
    
    private String website;
    
    private Double averageRating = 0.0;
    private Integer reviewCount = 0;
    
    private Boolean isVerified = false;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Event> events = new HashSet<>();
    
    @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Campaign> campaigns = new HashSet<>();
    
    @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<OrganizerRating> ratings = new HashSet<>();
}
