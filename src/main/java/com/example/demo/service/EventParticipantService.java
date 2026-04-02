package com.example.demo.service;

import com.example.demo.dto.EventParticipantDTO;
import com.example.demo.entity.ParticipationStatus;
import java.util.List;

public interface EventParticipantService {
    EventParticipantDTO registerParticipant(Long eventId, Long participantId);
    void unregisterParticipant(Long eventId, Long participantId);
    List<EventParticipantDTO> getEventParticipants(Long eventId);
    List<EventParticipantDTO> getParticipantEvents(Long participantId);
    void updateParticipationStatus(Long eventParticipantId, ParticipationStatus status);
    int getEventParticipantCount(Long eventId);
}
