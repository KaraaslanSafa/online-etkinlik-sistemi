package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.OrganizerRatingDTO;

public interface OrganizerRatingService {
    
    OrganizerRatingDTO createRating(Long organizerId, Long participantId, Integer rating, String comment);
    OrganizerRatingDTO getRating(Long id);
    List<OrganizerRatingDTO> getRatingsByOrganizer(Long organizerId);
    List<OrganizerRatingDTO> getRatingsByParticipant(Long participantId);
    List<OrganizerRatingDTO> getRatingsByOrganizerAndMinRating(Long organizerId, Integer minRating);
    OrganizerRatingDTO updateRating(Long id, Integer rating, String comment);
    void deleteRating(Long id);
}
