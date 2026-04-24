package com.example.demo.service;

import com.example.demo.dto.EventReviewDTO;
import java.util.List;

public interface EventReviewService {
    EventReviewDTO addReview(EventReviewDTO reviewDTO);
    List<EventReviewDTO> getReviewsByEvent(Long eventId);
}