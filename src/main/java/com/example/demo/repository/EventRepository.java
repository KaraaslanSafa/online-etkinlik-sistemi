package com.example.demo.repository;

import com.example.demo.entity.Event;
import com.example.demo.entity.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(EventStatus status);
    
    List<Event> findByCategoryId(Long categoryId);
    
    @Query("SELECT e FROM Event e WHERE e.status = :status AND LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Event> searchByKeywordAndStatus(@Param("keyword") String keyword, @Param("status") EventStatus status);
    
    @Query("SELECT e FROM Event e WHERE e.startDate >= :startDate AND e.startDate <= :endDate")
    List<Event> findEventsBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
