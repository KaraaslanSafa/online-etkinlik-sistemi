package com.example.demo.controller;

import com.example.demo.dto.EventDTO;
import com.example.demo.entity.EventStatus;
import com.example.demo.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Etkinlikler", description = "Etkinlik yönetimi API uç noktaları")
public class EventController {
    
    private final EventService eventService;
    
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }
    
    @PostMapping
    @Operation(summary = "Yeni etkinlik oluştur", description = "Yeni bir etkinlik oluşturur ve veritabanına ekler")
    @ApiResponse(responseCode = "201", description = "Etkinlik başarıyla oluşturuldu")
    @ApiResponse(responseCode = "400", description = "Geçersiz giriş")
    public ResponseEntity<EventDTO> createEvent(@Valid @RequestBody EventDTO eventDTO) {
        EventDTO createdEvent = eventService.createEvent(eventDTO);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Etkinliği ID ile getir", description = "Belirtilen ID'ye sahip etkinliği getirir")
    @ApiResponse(responseCode = "200", description = "Etkinlik bulundu")
    @ApiResponse(responseCode = "404", description = "Etkinlik bulunamadı")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long id) {
        EventDTO event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }
    
    @GetMapping
    @Operation(summary = "Tüm etkinlikleri getir", description = "Sistemdeki tüm etkinlikleri listeler")
    @ApiResponse(responseCode = "200", description = "Etkinlikler başarıyla alındı")
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        List<EventDTO> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Duruma göre etkinlikleri getir", description = "Belirtilen duruma sahip etkinlikleri listeler")
    @ApiResponse(responseCode = "200", description = "Etkinlikler başarıyla alındı")
    public ResponseEntity<List<EventDTO>> getEventsByStatus(@PathVariable EventStatus status) {
        List<EventDTO> events = eventService.getEventsByStatus(status);
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Kategoriye göre etkinlikleri getir", description = "Belirtilen kategorideki etkinlikleri listeler")
    @ApiResponse(responseCode = "200", description = "Etkinlikler başarıyla alındı")
    public ResponseEntity<List<EventDTO>> getEventsByCategory(@PathVariable Long categoryId) {
        List<EventDTO> events = eventService.getEventsByCategory(categoryId);
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Etkinlik ara", description = "Anahtar kelime ve duruma göre etkinlikleri arar")
    @ApiResponse(responseCode = "200", description = "Arama başarıyla yapıldı")
    public ResponseEntity<List<EventDTO>> searchEvents(
            @RequestParam String keyword,
            @RequestParam(required = false) EventStatus status) {
        List<EventDTO> events = eventService.searchEvents(keyword, status != null ? status : EventStatus.PLANNED);
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/between")
    @Operation(summary = "Tarih aralığında etkinlikleri getir", description = "Belirtilen tarih aralığındaki etkinlikleri listeler")
    @ApiResponse(responseCode = "200", description = "Etkinlikler başarıyla alındı")
    public ResponseEntity<List<EventDTO>> getEventsBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<EventDTO> events = eventService.getEventsBetween(startDate, endDate);
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/city/{city}")
    @Operation(summary = "Şehire göre etkinlikleri getir", description = "Belirtilen şehirdeki etkinlikleri listeler")
    @ApiResponse(responseCode = "200", description = "Etkinlikler başarıyla alındı")
    public ResponseEntity<List<EventDTO>> getEventsByCity(@PathVariable String city) {
        List<EventDTO> events = eventService.getEventsByCity(city);
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/free-events")
    @Operation(summary = "Ücretsiz etkinlikleri getir", description = "Tüm ücretsiz etkinlikleri listeler")
    @ApiResponse(responseCode = "200", description = "Etkinlikler başarıyla alındı")
    public ResponseEntity<List<EventDTO>> getFreeEvents() {
        List<EventDTO> events = eventService.getFreeEvents();
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/city/{city}/free")
    @Operation(summary = "Şehirdeki ücretsiz etkinlikleri getir", description = "Belirtilen şehirdeki ücretsiz etkinlikleri listeler")
    @ApiResponse(responseCode = "200", description = "Etkinlikler başarıyla alındı")
    public ResponseEntity<List<EventDTO>> getFreeEventsByCity(@PathVariable String city) {
        List<EventDTO> events = eventService.getFreeEventsByCity(city);
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/max-price")
    @Operation(summary = "Maksimum fiyata göre etkinlikleri getir", description = "Belirtilen fiyattan daha ucuz etkinlikleri listeler")
    @ApiResponse(responseCode = "200", description = "Etkinlikler başarıyla alındı")
    public ResponseEntity<List<EventDTO>> getEventsByMaxPrice(@RequestParam Double maxPrice) {
        List<EventDTO> events = eventService.getEventsByMaxPrice(maxPrice);
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/advanced-search")
    @Operation(summary = "Gelişmiş Arama ve Sıralama", description = "Müşteriler için şehre, fiyata ve yıldıza göre etkinlikleri filtreler ve istenilen kritere göre sıralar")
    @ApiResponse(responseCode = "200", description = "Filtreleme başarıyla uygulandı")
    public ResponseEntity<List<EventDTO>> advancedSearch(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false, defaultValue = "date") String sortBy,       // price, rating, date
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) { // asc, desc
        
        try {
            List<EventDTO> events = eventService.advancedSearch(city, minPrice, maxPrice, minRating, sortBy, sortDirection);
            return ResponseEntity.ok(events); 
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Etkinliği güncelle", description = "Belirtilen ID'ye sahip etkinliği günceller")
    @ApiResponse(responseCode = "200", description = "Etkinlik başarıyla güncellendi")
    @ApiResponse(responseCode = "404", description = "Etkinlik bulunamadı")
    public ResponseEntity<EventDTO> updateEvent(@PathVariable Long id, 
                                                @Valid @RequestBody EventDTO eventDTO) {
        EventDTO updatedEvent = eventService.updateEvent(id, eventDTO);
        return ResponseEntity.ok(updatedEvent);
    }
    
    @PatchMapping("/{id}/status")
    @Operation(summary = "Etkinlik durumunu güncelle", description = "Belirtilen etkinliğin durumunu günceller")
    @ApiResponse(responseCode = "200", description = "Durum başarıyla güncellendi")
    public ResponseEntity<Void> updateEventStatus(@PathVariable Long id, 
                                                  @RequestParam EventStatus status) {
        eventService.updateEventStatus(id, status);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Etkinliği sil", description = "Belirtilen ID'ye sahip etkinliği siler")
    @ApiResponse(responseCode = "204", description = "Etkinlik başarıyla silindi")
    @ApiResponse(responseCode = "404", description = "Etkinlik bulunamadı")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
