package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class EventDTO {
    private Long id;
    
    @NotBlank(message = "Etkinlik adı boş olamaz")
    private String title;
    
    private String description;
    
    @NotNull(message = "Başlangıç tarihi boş olamaz")
    private LocalDateTime startDate;
    
    @NotNull(message = "Bitiş tarihi boş olamaz")
    private LocalDateTime endDate;
    
    @NotBlank(message = "Lokasyon boş olamaz")
    private String location;
    
    private Integer capacity;
    
    @NotNull(message = "Kategori ID boş olamaz")
    private Long categoryId;
    
    private String status;
    
    private Integer participantCount;
    
    public EventDTO() {
    }
    
    public EventDTO(Long id, String title, String description, LocalDateTime startDate, 
                    LocalDateTime endDate, String location, Integer capacity, Long categoryId, 
                    String status, Integer participantCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.capacity = capacity;
        this.categoryId = categoryId;
        this.status = status;
        this.participantCount = participantCount;
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
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getParticipantCount() {
        return participantCount;
    }
    
    public void setParticipantCount(Integer participantCount) {
        this.participantCount = participantCount;
    }
}
