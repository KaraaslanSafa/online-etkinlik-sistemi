package com.example.demo.service;

import com.example.demo.dto.ParticipantDTO;
import com.example.demo.entity.Participant;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ParticipantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParticipantServiceImpl implements ParticipantService {
    
    private final ParticipantRepository participantRepository;
    
    public ParticipantServiceImpl(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }
    
    @Override
    public ParticipantDTO createParticipant(ParticipantDTO participantDTO) {
        Participant participant = new Participant();
        participant.setFirstName(participantDTO.getFirstName());
        participant.setLastName(participantDTO.getLastName());
        participant.setEmail(participantDTO.getEmail());
        participant.setPhoneNumber(participantDTO.getPhoneNumber());
        
        Participant savedParticipant = participantRepository.save(participant);
        return convertToDTO(savedParticipant);
    }
    
    @Override
    public ParticipantDTO updateParticipant(Long id, ParticipantDTO participantDTO) {
        Participant participant = participantRepository.findById(id)
            .orElseThrow(() -> ResourceNotFoundException.participantNotFound(id));
        
        participant.setFirstName(participantDTO.getFirstName());
        participant.setLastName(participantDTO.getLastName());
        participant.setEmail(participantDTO.getEmail());
        participant.setPhoneNumber(participantDTO.getPhoneNumber());
        
        Participant updatedParticipant = participantRepository.save(participant);
        return convertToDTO(updatedParticipant);
    }
    
    @Override
    public ParticipantDTO getParticipantById(Long id) {
        Participant participant = participantRepository.findById(id)
            .orElseThrow(() -> ResourceNotFoundException.participantNotFound(id));
        return convertToDTO(participant);
    }
    
    @Override
    public List<ParticipantDTO> getAllParticipants() {
        return participantRepository.findAll()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public void deleteParticipant(Long id) {
        if (!participantRepository.existsById(id)) {
            throw ResourceNotFoundException.participantNotFound(id);
        }
        participantRepository.deleteById(id);
    }
    
    @Override
    public ParticipantDTO getParticipantByEmail(String email) {
        Participant participant = participantRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Email ile katılımcı bulunamadı: " + email));
        return convertToDTO(participant);
    }
    
    private ParticipantDTO convertToDTO(Participant participant) {
        return new ParticipantDTO(participant.getId(), participant.getFirstName(), 
                                 participant.getLastName(), participant.getEmail(), participant.getPhoneNumber());
    }
}
