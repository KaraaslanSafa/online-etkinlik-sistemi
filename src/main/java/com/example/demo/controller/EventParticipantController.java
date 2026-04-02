package com.example.demo.controller;

import com.example.demo.dto.EventParticipantDTO;
import com.example.demo.entity.ParticipationStatus;
import com.example.demo.service.EventParticipantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/event-participants")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EventParticipantController {
    
    private final EventParticipantService eventParticipantService;
    
    public EventParticipantController(EventParticipantService eventParticipantService) {
        this.eventParticipantService = eventParticipantService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<EventParticipantDTO> registerParticipant(
            @RequestParam Long eventId,
            @RequestParam Long participantId) {
        EventParticipantDTO registration = eventParticipantService.registerParticipant(eventId, participantId);
        return new ResponseEntity<>(registration, HttpStatus.CREATED);
    }
    
    @DeleteMapping("/unregister")
    public ResponseEntity<Void> unregisterParticipant(
            @RequestParam Long eventId,
            @RequestParam Long participantId) {
        eventParticipantService.unregisterParticipant(eventId, participantId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<EventParticipantDTO>> getEventParticipants(@PathVariable Long eventId) {
        List<EventParticipantDTO> participants = eventParticipantService.getEventParticipants(eventId);
        return ResponseEntity.ok(participants);
    }
    
    @GetMapping("/participant/{participantId}")
    public ResponseEntity<List<EventParticipantDTO>> getParticipantEvents(@PathVariable Long participantId) {
        List<EventParticipantDTO> events = eventParticipantService.getParticipantEvents(participantId);
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/event/{eventId}/count")
    public ResponseEntity<Integer> getEventParticipantCount(@PathVariable Long eventId) {
        int count = eventParticipantService.getEventParticipantCount(eventId);
        return ResponseEntity.ok(count);
    }
    
    @PatchMapping("/{eventParticipantId}/status")
    public ResponseEntity<Void> updateParticipationStatus(
            @PathVariable Long eventParticipantId,
            @RequestParam ParticipationStatus status) {
        eventParticipantService.updateParticipationStatus(eventParticipantId, status);
        return ResponseEntity.ok().build();
    }
}
