package com.example.demo.service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.dto.EventParticipantDTO;
import com.example.demo.entity.Event;
import com.example.demo.entity.EventParticipant;
import com.example.demo.entity.Participant;
import com.example.demo.entity.ParticipationStatus;
import com.example.demo.exception.DuplicateRegistrationException;
import com.example.demo.exception.EventFullException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.TimeConflictException;
import com.example.demo.repository.EventParticipantRepository;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.ParticipantRepository;

@Service
public class EventParticipantServiceImpl implements EventParticipantService {
    
    private final EventParticipantRepository eventParticipantRepository;
    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final EmailService emailService;
    
    public EventParticipantServiceImpl(EventParticipantRepository eventParticipantRepository,
                                      EventRepository eventRepository,
                                      ParticipantRepository participantRepository,
                                      EmailService emailService) {
        this.eventParticipantRepository = eventParticipantRepository;
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
        this.emailService = emailService;
    }
    
    @Override
    public EventParticipantDTO registerParticipant(Long eventId, Long participantId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> ResourceNotFoundException.eventNotFound(eventId));
        
        Participant participant = participantRepository.findById(participantId)
            .orElseThrow(() -> ResourceNotFoundException.participantNotFound(participantId));
        
        // Already registered check
        if (eventParticipantRepository.existsByEventIdAndParticipantId(eventId, participantId)) {
            throw new DuplicateRegistrationException(eventId, participantId);
        }
        
        // Check if event has space
        if (!event.isAvailable()) {
            throw new EventFullException(event.getTitle(), event.getCapacity());
        }
        
        // 🔴 ZAM AN ÇAKIŞMASI KONTROLÜ 🔴
        // Katılımcının aynı saatinde başka bir etkinliğe kayıtlı olup olmadığını kontrol et
        List<Event> conflictingEvents = eventRepository.findConflictingEvents(
            eventId, event.getStartDate(), event.getEndDate()
        );
        
        for (Event conflictingEvent : conflictingEvents) {
            if (eventParticipantRepository.existsByEventIdAndParticipantId(conflictingEvent.getId(), participantId)) {
                throw new TimeConflictException(eventId, participantId);
            }
        }
        
        EventParticipant eventParticipant = new EventParticipant(event, participant);
        EventParticipant savedParticipant = eventParticipantRepository.save(eventParticipant);
        
        // 📧 KAYIT ONAY E-POSTASI GÖNDER 📧
        String participantFullName = participant.getFirstName() + " " + participant.getLastName();
        String eventDate = event.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        emailService.sendEventRegistrationConfirmation(
            participant.getEmail(),
            participantFullName,
            event.getTitle(),
            eventDate
        );
        
        return convertToDTO(savedParticipant);
    }
    
    @Override
    public void unregisterParticipant(Long eventId, Long participantId) {
        EventParticipant eventParticipant = eventParticipantRepository
            .findByEventIdAndParticipantId(eventId, participantId)
            .orElseThrow(() -> ResourceNotFoundException.participantNotRegistered(eventId, participantId));
        
        eventParticipantRepository.delete(eventParticipant);
    }
    
    @Override
    public List<EventParticipantDTO> getEventParticipants(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw ResourceNotFoundException.eventNotFound(eventId);
        }
        
        return eventParticipantRepository.findByEventId(eventId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<EventParticipantDTO> getParticipantEvents(Long participantId) {
        if (!participantRepository.existsById(participantId)) {
            throw ResourceNotFoundException.participantNotFound(participantId);
        }
        
        return eventParticipantRepository.findByParticipantId(participantId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public void updateParticipationStatus(Long eventParticipantId, ParticipationStatus status) {
        EventParticipant eventParticipant = eventParticipantRepository.findById(eventParticipantId)
            .orElseThrow(() -> new ResourceNotFoundException("Event-Participant kaydı bulunamadı: " + eventParticipantId));
        
        eventParticipant.setStatus(status);
        eventParticipantRepository.save(eventParticipant);
    }
    
    @Override
    public int getEventParticipantCount(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw ResourceNotFoundException.eventNotFound(eventId);
        }
        return eventParticipantRepository.countByEventId(eventId);
    }
    
    private EventParticipantDTO convertToDTO(EventParticipant eventParticipant) {
        return new EventParticipantDTO(
            eventParticipant.getId(),
            eventParticipant.getEvent().getId(),
            eventParticipant.getParticipant().getId(),
            eventParticipant.getParticipant().getFirstName() + " " + eventParticipant.getParticipant().getLastName(),
            eventParticipant.getParticipant().getEmail(),
            eventParticipant.getStatus(),
            eventParticipant.getRegisteredAt()
        );
    }
}
