package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.EventOrganizerDTO;

public interface EventOrganizerService {
    
    EventOrganizerDTO createOrganizer(String name, String email, String phone, String bio);
    EventOrganizerDTO getOrganizer(Long id);
    EventOrganizerDTO getOrganizerByEmail(String email);
    List<EventOrganizerDTO> getAllOrganizers();
    List<EventOrganizerDTO> getVerifiedOrganizers();
    List<EventOrganizerDTO> getTopRatedOrganizers();
    List<EventOrganizerDTO> getMostActiveOrganizers();
    List<EventOrganizerDTO> getOrganizersByMinRating(Double minRating);
    EventOrganizerDTO updateOrganizer(Long id, String name, String email, String phone, String bio, String website);
    void verifyOrganizer(Long id);
    void deleteOrganizer(Long id);
}
