package com.example.demo.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.EventOrganizerDTO;
import com.example.demo.entity.EventOrganizer;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.EventOrganizerRepository;
import com.example.demo.service.EventOrganizerService;

@Service
public class EventOrganizerServiceImpl implements EventOrganizerService {
    
    @Autowired
    private EventOrganizerRepository organizerRepository;
    
    @Override
    @Transactional
    public EventOrganizerDTO createOrganizer(String name, String email, String phone, String bio) {
        EventOrganizer organizer = new EventOrganizer();
        organizer.setName(name);
        organizer.setEmail(email);
        organizer.setPhone(phone);
        organizer.setBio(bio);
        organizer.setCreatedAt(LocalDateTime.now());
        
        EventOrganizer saved = organizerRepository.save(organizer);
        return convertToDTO(saved);
    }
    
    @Override
    public EventOrganizerDTO getOrganizer(Long id) {
        EventOrganizer organizer = organizerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Organizer not found with id: " + id));
        return convertToDTO(organizer);
    }
    
    @Override
    public EventOrganizerDTO getOrganizerByEmail(String email) {
        EventOrganizer organizer = organizerRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Organizer not found with email: " + email));
        return convertToDTO(organizer);
    }
    
    @Override
    public List<EventOrganizerDTO> getAllOrganizers() {
        return organizerRepository.findAll()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<EventOrganizerDTO> getVerifiedOrganizers() {
        return organizerRepository.findAllVerified()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<EventOrganizerDTO> getTopRatedOrganizers() {
        return organizerRepository.findTopRatedOrganizers()
            .stream()
            .limit(10)
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<EventOrganizerDTO> getMostActiveOrganizers() {
        return organizerRepository.findMostActiveOrganizers()
            .stream()
            .limit(10)
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<EventOrganizerDTO> getOrganizersByMinRating(Double minRating) {
        return organizerRepository.findByMinRating(minRating)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public EventOrganizerDTO updateOrganizer(Long id, String name, String email, String phone, String bio, String website) {
        EventOrganizer organizer = organizerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Organizer not found with id: " + id));
        
        organizer.setName(name);
        organizer.setEmail(email);
        organizer.setPhone(phone);
        organizer.setBio(bio);
        organizer.setWebsite(website);
        organizer.setUpdatedAt(LocalDateTime.now());
        
        EventOrganizer updated = organizerRepository.save(organizer);
        return convertToDTO(updated);
    }
    
    @Override
    @Transactional
    public void verifyOrganizer(Long id) {
        EventOrganizer organizer = organizerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Organizer not found with id: " + id));
        
        organizer.setIsVerified(true);
        organizer.setUpdatedAt(LocalDateTime.now());
        organizerRepository.save(organizer);
    }
    
    @Override
    @Transactional
    public void deleteOrganizer(Long id) {
        if (!organizerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Organizer not found with id: " + id);
        }
        organizerRepository.deleteById(id);
    }
    
    private EventOrganizerDTO convertToDTO(EventOrganizer organizer) {
        EventOrganizerDTO dto = new EventOrganizerDTO();
        dto.setId(organizer.getId());
        dto.setName(organizer.getName());
        dto.setEmail(organizer.getEmail());
        dto.setPhone(organizer.getPhone());
        dto.setBio(organizer.getBio());
        dto.setFoundedYear(organizer.getFoundedYear());
        dto.setTotalEvents(organizer.getTotalEvents());
        dto.setTotalParticipants(organizer.getTotalParticipants());
        dto.setWebsite(organizer.getWebsite());
        dto.setAverageRating(organizer.getAverageRating());
        dto.setReviewCount(organizer.getReviewCount());
        dto.setIsVerified(organizer.getIsVerified());
        return dto;
    }
}
