package com.example.demo.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.EventReviewDTO;
import com.example.demo.entity.Event;
import com.example.demo.entity.EventReview;
import com.example.demo.entity.Participant;
import com.example.demo.exception.DuplicateRegistrationException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.EventReviewRepository;
import com.example.demo.repository.ParticipantRepository;
import com.example.demo.service.EventReviewService;

/**
 * EventReviewService Implementation
 */
@Service
public class EventReviewServiceImpl implements EventReviewService {
    
    @Autowired
    private EventReviewRepository reviewRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private ParticipantRepository participantRepository;
    
    @Override
    @Transactional
    public EventReviewDTO createReview(Long eventId, Long participantId, Integer rating, String title, String comment) {
        // Check if user already reviewed this event
        if (reviewRepository.existsByEventIdAndParticipantId(eventId, participantId)) {
            throw new DuplicateRegistrationException("Bu etkinlik için zaten bir değerlendirme yaptınız");
        }
        
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        
        Participant participant = participantRepository.findById(participantId)
            .orElseThrow(() -> new ResourceNotFoundException("Participant not found with id: " + participantId));
        
        EventReview review = new EventReview();
        review.setEvent(event);
        review.setParticipant(participant);
        review.setRating(rating);
        review.setTitle(title);
        review.setComment(comment);
        
        EventReview savedReview = reviewRepository.save(review);
        
        // Update event average rating
        updateEventRating(eventId);
        
        return convertToDTO(savedReview);
    }
    
    @Override
    public EventReviewDTO getReview(Long id) {
        EventReview review = reviewRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        return convertToDTO(review);
    }
    
    @Override
    public List<EventReviewDTO> getReviewsByEvent(Long eventId) {
        return reviewRepository.findByEventId(eventId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<EventReviewDTO> getReviewsByParticipant(Long participantId) {
        return reviewRepository.findByParticipantId(participantId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<EventReviewDTO> getReviewsByEventAndMinRating(Long eventId, Integer minRating) {
        return reviewRepository.findByEventIdAndMinRating(eventId, minRating)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<EventReviewDTO> getMostHelpfulReviews(Long eventId) {
        return reviewRepository.findMostHelpfulReviewsByEventId(eventId)
            .stream()
            .limit(5)
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public EventReviewDTO updateReview(Long id, Integer rating, String title, String comment) {
        EventReview review = reviewRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        
        review.setRating(rating);
        review.setTitle(title);
        review.setComment(comment);
        review.setUpdatedAt(LocalDateTime.now());
        
        EventReview updated = reviewRepository.save(review);
        
        // Update event rating
        updateEventRating(review.getEvent().getId());
        
        return convertToDTO(updated);
    }
    
    @Override
    @Transactional
    public void deleteReview(Long id) {
        EventReview review = reviewRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        
        Long eventId = review.getEvent().getId();
        reviewRepository.deleteById(id);
        
        // Update event rating
        updateEventRating(eventId);
    }
    
    @Override
    @Transactional
    public void markAsHelpful(Long id) {
        EventReview review = reviewRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        
        review.setHelpfulCount(review.getHelpfulCount() + 1);
        reviewRepository.save(review);
    }
    
    @Override
    public Double getEventAverageRating(Long eventId) {
        Double avgRating = reviewRepository.getAverageRatingByEventId(eventId);
        return avgRating != null ? avgRating : 0.0;
    }
    
    /**
     * Event'in ortalama rating ve review count'ını güncelle
     */
    private void updateEventRating(Long eventId) {
        Double avgRating = reviewRepository.getAverageRatingByEventId(eventId);
        Integer reviewCount = reviewRepository.getReviewCountByEventId(eventId);
        
        Event event = eventRepository.findById(eventId).orElseThrow();
        event.setAverageRating(avgRating != null ? avgRating : 0.0);
        event.setReviewCount(reviewCount != null ? reviewCount : 0);
        eventRepository.save(event);
    }
    
    private EventReviewDTO convertToDTO(EventReview review) {
        EventReviewDTO dto = new EventReviewDTO();
        dto.setId(review.getId());
        dto.setEventId(review.getEvent().getId());
        dto.setEventTitle(review.getEvent().getTitle());
        dto.setParticipantId(review.getParticipant().getId());
        dto.setParticipantName(maskParticipantName(review.getParticipant().getFirstName() + " " + review.getParticipant().getLastName()));
        dto.setRating(review.getRating());
        dto.setTitle(review.getTitle());
        dto.setComment(review.getComment());
        dto.setHelpfulCount(review.getHelpfulCount());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());
        return dto;
    }
    
    /**
     * Privacy: Hide full name, show only first letter
     */
    private String maskParticipantName(String fullName) {
        if (fullName == null || fullName.isEmpty()) return "Anonim";
        String[] parts = fullName.split(" ");
        return parts[0].charAt(0) + "***";
    }
}
