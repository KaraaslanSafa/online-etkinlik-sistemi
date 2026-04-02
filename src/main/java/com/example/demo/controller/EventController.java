package com.example.demo.controller;

import com.example.demo.dto.EventDTO;
import com.example.demo.entity.EventStatus;
import com.example.demo.service.EventService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EventController {
    
    private final EventService eventService;
    
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }
    
    @PostMapping
    public ResponseEntity<EventDTO> createEvent(@Valid @RequestBody EventDTO eventDTO) {
        EventDTO createdEvent = eventService.createEvent(eventDTO);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long id) {
        EventDTO event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }
    
    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        List<EventDTO> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<EventDTO>> getEventsByStatus(@PathVariable EventStatus status) {
        List<EventDTO> events = eventService.getEventsByStatus(status);
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<EventDTO>> getEventsByCategory(@PathVariable Long categoryId) {
        List<EventDTO> events = eventService.getEventsByCategory(categoryId);
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<EventDTO>> searchEvents(
            @RequestParam String keyword,
            @RequestParam(required = false) EventStatus status) {
        List<EventDTO> events = eventService.searchEvents(keyword, status != null ? status : EventStatus.PLANNED);
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/between")
    public ResponseEntity<List<EventDTO>> getEventsBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<EventDTO> events = eventService.getEventsBetween(startDate, endDate);
        return ResponseEntity.ok(events);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> updateEvent(@PathVariable Long id, 
                                                @Valid @RequestBody EventDTO eventDTO) {
        EventDTO updatedEvent = eventService.updateEvent(id, eventDTO);
        return ResponseEntity.ok(updatedEvent);
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateEventStatus(@PathVariable Long id, 
                                                  @RequestParam EventStatus status) {
        eventService.updateEventStatus(id, status);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
