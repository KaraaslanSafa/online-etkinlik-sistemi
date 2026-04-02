package com.example.demo.controller;

import com.example.demo.dto.ParticipantDTO;
import com.example.demo.service.ParticipantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participants")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ParticipantController {
    
    private final ParticipantService participantService;
    
    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }
    
    @PostMapping
    public ResponseEntity<ParticipantDTO> createParticipant(@Valid @RequestBody ParticipantDTO participantDTO) {
        ParticipantDTO createdParticipant = participantService.createParticipant(participantDTO);
        return new ResponseEntity<>(createdParticipant, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ParticipantDTO> getParticipantById(@PathVariable Long id) {
        ParticipantDTO participant = participantService.getParticipantById(id);
        return ResponseEntity.ok(participant);
    }
    
    @GetMapping
    public ResponseEntity<List<ParticipantDTO>> getAllParticipants() {
        List<ParticipantDTO> participants = participantService.getAllParticipants();
        return ResponseEntity.ok(participants);
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<ParticipantDTO> getParticipantByEmail(@PathVariable String email) {
        ParticipantDTO participant = participantService.getParticipantByEmail(email);
        return ResponseEntity.ok(participant);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ParticipantDTO> updateParticipant(@PathVariable Long id, 
                                                            @Valid @RequestBody ParticipantDTO participantDTO) {
        ParticipantDTO updatedParticipant = participantService.updateParticipant(id, participantDTO);
        return ResponseEntity.ok(updatedParticipant);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParticipant(@PathVariable Long id) {
        participantService.deleteParticipant(id);
        return ResponseEntity.noContent().build();
    }
}
