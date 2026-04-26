package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.dto.EventDTO;
import com.example.demo.entity.Category;
import com.example.demo.entity.Event;
import com.example.demo.entity.EventStatus;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.entity.ApprovalStatus;

@Service
public class EventServiceImpl implements EventService {
    
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    
    public EventServiceImpl(EventRepository eventRepository, CategoryRepository categoryRepository) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
    }
    
    @Override
    public EventDTO createEvent(EventDTO eventDTO) {
        Category category = categoryRepository.findById(eventDTO.getCategoryId())
            .orElseThrow(() -> ResourceNotFoundException.categoryNotFound(eventDTO.getCategoryId()));
        
        Event event = new Event();
        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        event.setStartDate(eventDTO.getStartDate());
        event.setEndDate(eventDTO.getEndDate());
        event.setLocation(eventDTO.getLocation());
        event.setCity(eventDTO.getCity());
        event.setPrice(eventDTO.getPrice());
        event.setIsFree(eventDTO.getIsFree());
        event.setCapacity(eventDTO.getCapacity());
        event.setCategory(category);
        
        Event savedEvent = eventRepository.save(event);
        return convertToDTO(savedEvent);
    }
    
    @Override
    public EventDTO updateEvent(Long id, EventDTO eventDTO) {
        Event event = eventRepository.findById(id)
            .orElseThrow(() -> ResourceNotFoundException.eventNotFound(id));
        
        if (eventDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(eventDTO.getCategoryId())
                .orElseThrow(() -> ResourceNotFoundException.categoryNotFound(eventDTO.getCategoryId()));
            event.setCategory(category);
        }
        
        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        event.setStartDate(eventDTO.getStartDate());
        event.setEndDate(eventDTO.getEndDate());
        event.setLocation(eventDTO.getLocation());
        event.setCity(eventDTO.getCity());
        event.setPrice(eventDTO.getPrice());
        event.setIsFree(eventDTO.getIsFree());
        event.setCapacity(eventDTO.getCapacity());
        event.setUpdatedAt(LocalDateTime.now());
        
        Event updatedEvent = eventRepository.save(event);
        return convertToDTO(updatedEvent);
    }
    
    @Override
    public EventDTO getEventById(Long id) {
        Event event = eventRepository.findById(id)
            .orElseThrow(() -> ResourceNotFoundException.eventNotFound(id));
        return convertToDTO(event);
    }
    
    @Override
    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<EventDTO> getEventsByStatus(EventStatus status) {
        return eventRepository.findByStatus(status)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<EventDTO> getEventsByCategory(Long categoryId) {
        return eventRepository.findByCategoryId(categoryId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<EventDTO> searchEvents(String keyword, EventStatus status) {
        return eventRepository.searchByKeywordAndStatus(keyword, status)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<EventDTO> getEventsBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return eventRepository.findEventsBetween(startDate, endDate)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<EventDTO> getEventsByCity(String city) {
        return eventRepository.findByCity(city)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<EventDTO> getFreeEvents() {
        return eventRepository.findByIsFreeTrue()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<EventDTO> getFreeEventsByCity(String city) {
        return eventRepository.findByCityAndIsFree(city, true)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<EventDTO> getEventsByMaxPrice(Double maxPrice) {
        return eventRepository.findByPriceLessThan(maxPrice)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw ResourceNotFoundException.eventNotFound(id);
        }
        eventRepository.deleteById(id);
    }
    
    @Override
    public void updateEventStatus(Long id, EventStatus status) {
        Event event = eventRepository.findById(id)
            .orElseThrow(() -> ResourceNotFoundException.eventNotFound(id));
        event.setStatus(status);
        event.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(event);
    }
    
    private EventDTO convertToDTO(Event event) {
        EventDTO dto = new EventDTO(
            event.getId(),
            event.getTitle(),
            event.getDescription(),
            event.getStartDate(),
            event.getEndDate(),
            event.getLocation(),
            event.getCity(),
            event.getPrice(),
            event.getIsFree(),
            event.getCapacity(),
            event.getCategory().getId(),
            event.getStatus().toString(),
            event.getParticipantCount()
        );
        
        // Approval Status alanlarını ekle
        if (event.getApprovalStatus() != null) {
            dto.setApprovalStatus(event.getApprovalStatus().toString());
        }
        dto.setApproverAdminId(event.getApproverAdminId());
        dto.setApprovedAt(event.getApprovedAt());
        dto.setRejectionReason(event.getRejectionReason());
        
        return dto;
    }
    
    @Override
    public List<EventDTO> getApprovalPendingEvents() {
        return eventRepository.findAll().stream()
            .filter(e -> e.getApprovalStatus() == ApprovalStatus.PENDING)
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public void approveEvent(Long eventId, Long adminId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> ResourceNotFoundException.eventNotFound(eventId));
        
        event.setApprovalStatus(ApprovalStatus.APPROVED);
        event.setApproverAdminId(adminId);
        event.setApprovedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(event);
    }
    
    @Override
    public void rejectEvent(Long eventId, String rejectionReason) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> ResourceNotFoundException.eventNotFound(eventId));
        
        event.setApprovalStatus(ApprovalStatus.REJECTED);
        event.setRejectionReason(rejectionReason);
        event.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(event);
    }
