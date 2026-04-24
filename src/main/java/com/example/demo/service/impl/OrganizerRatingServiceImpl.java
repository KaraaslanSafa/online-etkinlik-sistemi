package com.example.demo.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.OrganizerRatingDTO;
import com.example.demo.entity.EventOrganizer;
import com.example.demo.entity.OrganizerRating;
import com.example.demo.entity.Participant;
import com.example.demo.exception.DuplicateRegistrationException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.EventOrganizerRepository;
import com.example.demo.repository.OrganizerRatingRepository;
import com.example.demo.repository.ParticipantRepository;
import com.example.demo.service.OrganizerRatingService;

@Service
public class OrganizerRatingServiceImpl implements OrganizerRatingService {
    
    @Autowired
    private OrganizerRatingRepository ratingRepository;
    
    @Autowired
    private EventOrganizerRepository organizerRepository;
    
    @Autowired
    private ParticipantRepository participantRepository;
    
    @Override
    @Transactional
    public OrganizerRatingDTO createRating(Long organizerId, Long participantId, Integer rating, String comment) {
        // Check if user already rated this organizer
        if (ratingRepository.existsByOrganizerIdAndParticipantId(organizerId, participantId)) {
            throw new DuplicateRegistrationException("Bu organizatör için zaten bir değerlendirme yaptınız");
        }
        
        EventOrganizer organizer = organizerRepository.findById(organizerId)
            .orElseThrow(() -> new ResourceNotFoundException("Organizer not found with id: " + organizerId));
        
        Participant participant = participantRepository.findById(participantId)
            .orElseThrow(() -> new ResourceNotFoundException("Participant not found with id: " + participantId));
        
        OrganizerRating organizerRating = new OrganizerRating();
        organizerRating.setOrganizer(organizer);
        organizerRating.setParticipant(participant);
        organizerRating.setRating(rating);
        organizerRating.setComment(comment);
        
        OrganizerRating savedRating = ratingRepository.save(organizerRating);
        
        // Update organizer average rating
        updateOrganizerRating(organizerId);
        
        return convertToDTO(savedRating);
    }
    
    @Override
    public OrganizerRatingDTO getRating(Long id) {
        OrganizerRating rating = ratingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Rating not found with id: " + id));
        return convertToDTO(rating);
    }
    
    @Override
    public List<OrganizerRatingDTO> getRatingsByOrganizer(Long organizerId) {
        return ratingRepository.findByOrganizerId(organizerId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<OrganizerRatingDTO> getRatingsByParticipant(Long participantId) {
        return ratingRepository.findByParticipantId(participantId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<OrganizerRatingDTO> getRatingsByOrganizerAndMinRating(Long organizerId, Integer minRating) {
        return ratingRepository.findByOrganizerIdAndMinRating(organizerId, minRating)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public OrganizerRatingDTO updateRating(Long id, Integer rating, String comment) {
        OrganizerRating organizerRating = ratingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Rating not found with id: " + id));
        
        organizerRating.setRating(rating);
        organizerRating.setComment(comment);
        
        OrganizerRating updated = ratingRepository.save(organizerRating);
        
        // Update organizer rating
        updateOrganizerRating(organizerRating.getOrganizer().getId());
        
        return convertToDTO(updated);
    }
    
    @Override
    @Transactional
    public void deleteRating(Long id) {
        OrganizerRating rating = ratingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Rating not found with id: " + id));
        
        Long organizerId = rating.getOrganizer().getId();
        ratingRepository.deleteById(id);
        
        // Update organizer rating
        updateOrganizerRating(organizerId);
    }
    
    private void updateOrganizerRating(Long organizerId) {
        Double avgRating = ratingRepository.getAverageRatingByOrganizerId(organizerId);
        Integer reviewCount = ratingRepository.getReviewCountByOrganizerId(organizerId);
        
        EventOrganizer organizer = organizerRepository.findById(organizerId).orElseThrow();
        organizer.setAverageRating(avgRating != null ? avgRating : 0.0);
        organizer.setReviewCount(reviewCount != null ? reviewCount : 0);
        organizerRepository.save(organizer);
    }
    
    private OrganizerRatingDTO convertToDTO(OrganizerRating rating) {
        OrganizerRatingDTO dto = new OrganizerRatingDTO();
        dto.setId(rating.getId());
        dto.setOrganizerId(rating.getOrganizer().getId());
        dto.setOrganizerName(rating.getOrganizer().getName());
        dto.setParticipantId(rating.getParticipant().getId());
        dto.setParticipantName(maskParticipantName(rating.getParticipant().getFirstName() + " " + rating.getParticipant().getLastName()));
        dto.setRating(rating.getRating());
        dto.setComment(rating.getComment());
        dto.setCreatedAt(rating.getCreatedAt());
        return dto;
    }
    
    private String maskParticipantName(String fullName) {
        if (fullName == null || fullName.isEmpty()) return "Anonim";
        String[] parts = fullName.split(" ");
        return parts[0].charAt(0) + "***";
    }
}
