package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "Events")
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Etkinlik adı boş olamaz")
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(length = 2000)
    private String description;
    
    @NotNull(message = "Başlangıç tarihi boş olamaz")
    @Column(nullable = false)
    private LocalDateTime startDate;
    
    @NotNull(message = "Bitiş tarihi boş olamaz")
    @Column(nullable = false)
    private LocalDateTime endDate;
    
    @NotBlank(message = "Lokasyon boş olamaz")
    @Column(nullable = false, length = 300)
    private String location;
    
    @Column(length = 100)
    private String city;
    
    @Column(name = "price_amount")
    private Double price = 0.0;
    
    @Column(columnDefinition = "BIT DEFAULT 0")
    private Boolean isFree = false;
    
    @Column(nullable = false)
    private Integer capacity = 100;
    
    @NotNull(message = "Kategori seçilmeli")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status = EventStatus.PLANNED;
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EventParticipant> eventParticipants = new HashSet<>();
    
    // Organizatör Bilgisi
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organizer_id")
    private EventOrganizer organizer;
    
    // Rating ve Review İstatistikleri
    @Column(columnDefinition = "DECIMAL(3,2) DEFAULT 0")
    private Double averageRating = 0.0;
    
    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer reviewCount = 0;
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<EventReview> reviews = new HashSet<>();
    
    @Column(nullable = false, columnDefinition = "DATETIME DEFAULT GETDATE()")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime updatedAt;
    
    public Event() {
    }
    
    public Event(String title, String description, LocalDateTime startDate, 
                 LocalDateTime endDate, String location, Integer capacity, Category category) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.capacity = capacity;
        this.category = category;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public Integer getCapacity() {
        return capacity;
    }
    
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public EventStatus getStatus() {
        return status;
    }
    
    public void setStatus(EventStatus status) {
        this.status = status;
    }
    
    public Set<EventParticipant> getEventParticipants() {
        return eventParticipants;
    }
    
    public void setEventParticipants(Set<EventParticipant> eventParticipants) {
        this.eventParticipants = eventParticipants;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public int getParticipantCount() {
        return eventParticipants.size();
    }
    
    public boolean isAvailable() {
        return getParticipantCount() < capacity;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    public Boolean getIsFree() {
        return isFree;
    }
    
    public void setIsFree(Boolean isFree) {
        this.isFree = isFree;
    }
    
    public EventOrganizer getOrganizer() {
        return organizer;
    }
    
    public void setOrganizer(EventOrganizer organizer) {
        this.organizer = organizer;
    }
    
    public Double getAverageRating() {
        return averageRating;
    }
    
    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }
    
    public Integer getReviewCount() {
        return reviewCount;
    }
    
    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }
    
    public Set<EventReview> getReviews() {
        return reviews;
    }
    
    public void setReviews(Set<EventReview> reviews) {
        this.reviews = reviews;
    }
}
