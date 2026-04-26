package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.dto.EventDTO;
import com.example.demo.entity.EventStatus;

public interface EventService {
    EventDTO createEvent(EventDTO eventDTO);
    EventDTO updateEvent(Long id, EventDTO eventDTO);
    EventDTO getEventById(Long id);
    List<EventDTO> getAllEvents();
    List<EventDTO> getEventsByStatus(EventStatus status);
    List<EventDTO> getEventsByCategory(Long categoryId);
    List<EventDTO> searchEvents(String keyword, EventStatus status);
    List<EventDTO> getEventsBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<EventDTO> getEventsByCity(String city);
    List<EventDTO> getFreeEvents();
    List<EventDTO> getFreeEventsByCity(String city);
    List<EventDTO> getEventsByMaxPrice(Double maxPrice);
    void deleteEvent(Long id);
    void deleteEventByAdmin(Long id, Long adminId);  // Admin tarafından silme
    void updateEventStatus(Long id, EventStatus status);
    
    // Admin Onay Metodları
    List<EventDTO> getApprovalPendingEvents();
    void approveEvent(Long eventId, Long adminId);
    void rejectEvent(Long eventId, String rejectionReason);
}
