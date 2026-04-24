package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.EventReviewDTO;

public interface EventReviewService {
    
    EventReviewDTO createReview(Long eventId, Long participantId, Integer rating, String title, String comment);
    EventReviewDTO getReview(Long id);
    List<EventReviewDTO> getReviewsByEvent(Long eventId);
    List<EventReviewDTO> getReviewsByParticipant(Long participantId);
    List<EventReviewDTO> getReviewsByEventAndMinRating(Long eventId, Integer minRating);
    List<EventReviewDTO> getMostHelpfulReviews(Long eventId);
    EventReviewDTO updateReview(Long id, Integer rating, String title, String comment);
    void deleteReview(Long id);
    void markAsHelpful(Long id);
    Double getEventAverageRating(Long eventId);
}
