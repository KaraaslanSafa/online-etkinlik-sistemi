package com.example.demo.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ticket - Etkinlik Biletleri
 */
@Entity
@Table(name = "Tickets", indexes = {
    @Index(name = "idx_ticket_number", columnList = "ticket_number"),
    @Index(name = "idx_event_id", columnList = "event_id"),
    @Index(name = "idx_participant_id", columnList = "participant_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;
    
    @Column(nullable = false, unique = true, length = 50)
    private String ticketNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(50) DEFAULT 'STANDARD'")
    private TicketType ticketType = TicketType.STANDARD;
    
    @Column
    private Double price = 0.0;
    
    @Column
    private Double discountAmount = 0.0;
    
    @Column
    private Double finalPrice = 0.0;
    
    @CreationTimestamp
    private LocalDateTime purchaseDate;
    
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(50) DEFAULT 'PENDING'")
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(50) DEFAULT 'NONE'")
    private PaymentMethod paymentMethod = PaymentMethod.NONE;
    
    @Column(length = 100)
    private String transactionId;
    
    @Column(length = 500)
    private String qrCodeUrl;
    
    @Column(columnDefinition = "BIT DEFAULT 0")
    private Boolean isUsed = false;
    
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime usedAt;
    
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime expiresAt;
    
    @Column(length = 500)
    private String notes;
}
