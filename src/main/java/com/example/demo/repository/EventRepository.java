package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Event;
import com.example.demo.entity.EventStatus;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(EventStatus status);
    
    List<Event> findByCategoryId(Long categoryId);
    
    // City-based filtering
    List<Event> findByCity(String city);
    
    List<Event> findByCityAndStatus(String city, EventStatus status);
    
    // Price-based filtering
    List<Event> findByIsFreeTrue();
    
    List<Event> findByIsFree(Boolean isFree);
    
    List<Event> findByCityAndIsFree(String city, Boolean isFree);
    
    @Query("SELECT e FROM Event e WHERE e.price < :price")
    List<Event> findByPriceLessThan(@Param("price") Double price);
    
    @Query("SELECT e FROM Event e WHERE e.city = :city AND e.price < :maxPrice")
    List<Event> findByCityAndMaxPrice(@Param("city") String city, @Param("maxPrice") Double maxPrice);
    
    @Query("SELECT e FROM Event e WHERE e.status = :status AND LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Event> searchByKeywordAndStatus(@Param("keyword") String keyword, @Param("status") EventStatus status);
    
    @Query("SELECT e FROM Event e WHERE e.startDate >= :startDate AND e.startDate <= :endDate")
    List<Event> findEventsBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Time conflict check - find events at same time
    @Query("SELECT e FROM Event e WHERE e.id != :eventId AND " +
           "NOT (e.endDate <= :startDate OR e.startDate >= :endDate)")
    List<Event> findConflictingEvents(@Param("eventId") Long eventId, 
                                     @Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);
    
    // ======================== SORTING / FILTERING METHODS ========================
    
    // Rating-based queries
    @Query("SELECT e FROM Event e ORDER BY e.averageRating DESC")
    List<Event> findAllOrderByRatingDesc();
    
    @Query("SELECT e FROM Event e WHERE e.averageRating >= :minRating ORDER BY e.averageRating DESC")
    List<Event> findByMinRatingOrderByRating(@Param("minRating") Double minRating);
    
    // Price-based sorting
    @Query("SELECT e FROM Event e WHERE e.city = :city ORDER BY e.price ASC")
    List<Event> findByCityOrderByPriceAsc(@Param("city") String city);
    
    @Query("SELECT e FROM Event e WHERE e.city = :city ORDER BY e.price DESC")
    List<Event> findByCityOrderByPriceDesc(@Param("city") String city);
    
    @Query("SELECT e FROM Event e ORDER BY e.price ASC")
    List<Event> findAllOrderByPriceAsc();
    
    @Query("SELECT e FROM Event e ORDER BY e.price DESC")
    List<Event> findAllOrderByPriceDesc();
    
    // Combined price and rating
    @Query("SELECT e FROM Event e WHERE e.price BETWEEN :minPrice AND :maxPrice ORDER BY e.price ASC")
    List<Event> findByPriceRangeOrderByPrice(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
    
    @Query("SELECT e FROM Event e WHERE e.price BETWEEN :minPrice AND :maxPrice ORDER BY e.averageRating DESC")
    List<Event> findByPriceRangeOrderByRating(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
    
    // Organizer-based queries
    List<Event> findByOrganizerIdOrderByCreatedAtDesc(Long organizerId);
    
    @Query("SELECT e FROM Event e WHERE e.organizer.id = :organizerId ORDER BY e.averageRating DESC")
    List<Event> findByOrganizerOrderByRating(@Param("organizerId") Long organizerId);
    
    // Date-based sorting
    @Query("SELECT e FROM Event e ORDER BY e.startDate ASC")
    List<Event> findAllOrderByStartDateAsc();
    
    @Query("SELECT e FROM Event e WHERE e.city = :city ORDER BY e.startDate ASC")
    List<Event> findByCityOrderByStartDate(@Param("city") String city);
    
    // Complex queries - City + Rating + Price
    @Query("SELECT e FROM Event e WHERE e.city = :city AND e.averageRating >= :minRating AND e.price <= :maxPrice ORDER BY e.averageRating DESC")
    List<Event> findByCityRatingAndPrice(@Param("city") String city, @Param("minRating") Double minRating, @Param("maxPrice") Double maxPrice);
    
    @Query("SELECT e FROM Event e WHERE e.city = :city AND e.isFree = :isFree ORDER BY e.averageRating DESC")
    List<Event> findByCityAndFreeOrderByRating(@Param("city") String city, @Param("isFree") Boolean isFree);
}
