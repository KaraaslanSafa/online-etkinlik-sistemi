package com.example.demo.service;

import com.example.demo.dto.ParticipantDTO;
import java.util.List;

public interface ParticipantService {
    ParticipantDTO createParticipant(ParticipantDTO participantDTO);
    ParticipantDTO updateParticipant(Long id, ParticipantDTO participantDTO);
    ParticipantDTO getParticipantById(Long id);
    List<ParticipantDTO> getAllParticipants();
    void deleteParticipant(Long id);
    ParticipantDTO getParticipantByEmail(String email);
}
