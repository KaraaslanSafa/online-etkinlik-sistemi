package com.example.demo.controller;

import com.example.demo.dto.EventReviewDTO;
import com.example.demo.service.EventReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Etkinlik Değerlendirmeleri", description = "Katılımcıların etkinliklere puan ve yorum bırakması")
public class EventReviewController {

    private final EventReviewService reviewService;
    
    public EventReviewController(EventReviewService reviewService) {
        this.reviewService = reviewService;
    }
    
    @PostMapping
    @Operation(summary = "Değerlendirme Ekle", description = "Etkinliğe 1-5 arası yıldız verip yorum yapar")
    public ResponseEntity<EventReviewDTO> addReview(@RequestBody EventReviewDTO reviewDTO) {
        EventReviewDTO createdReview = reviewService.addReview(reviewDTO);
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
    }
}