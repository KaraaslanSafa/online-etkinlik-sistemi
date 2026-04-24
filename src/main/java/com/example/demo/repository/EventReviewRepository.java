package com.example.demo.repository;

import com.example.demo.entity.EventReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventReviewRepository extends JpaRepository<EventReview, Long> {
    List<EventReview> findByEventId(Long eventId);
}