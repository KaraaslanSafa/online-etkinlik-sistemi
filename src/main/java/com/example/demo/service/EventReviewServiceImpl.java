package com.example.demo.service;

import com.example.demo.dto.EventReviewDTO;
import com.example.demo.entity.Event;
import com.example.demo.entity.EventReview;
import com.example.demo.entity.Participant;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.EventReviewRepository;
import com.example.demo.repository.ParticipantRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventReviewServiceImpl implements EventReviewService {

    private final EventReviewRepository reviewRepository;
    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;

    public EventReviewServiceImpl(EventReviewRepository reviewRepository,
                                  EventRepository eventRepository,
                                  ParticipantRepository participantRepository) {
        this.reviewRepository = reviewRepository;
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
    }

    @Override
    public EventReviewDTO addReview(EventReviewDTO dto) {
        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new RuntimeException("Etkinlik bulunamadı"));
        Participant participant = participantRepository.findById(dto.getParticipantId())
                .orElseThrow(() -> new RuntimeException("Katılımcı bulunamadı"));

        EventReview review = new EventReview();
        review.setEvent(event);
        review.setParticipant(participant);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setCreatedAt(LocalDateTime.now());

        review = reviewRepository.save(review);
        dto.setId(review.getId());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }

    @Override
    public List<EventReviewDTO> getReviewsByEvent(Long eventId) {
        return reviewRepository.findByEventId(eventId)
                .stream()
                // Burada manuel DTO dönüşümü yapabilirsiniz veya modelmapper kullanabilirsiniz.
                .map(r -> new EventReviewDTO()) 
                .collect(Collectors.toList());
    }
}