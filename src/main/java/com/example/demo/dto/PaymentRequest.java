package com.example.demo.dto;

public class PaymentRequest {
    private Long eventId;
    private Long participantId;
    private String cardNumber;
    private String cardHolderName;
    private String cvv;
    private Double amount;

    // Getters and Setters
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public Long getParticipantId() { return participantId; }
    public void setParticipantId(Long participantId) { this.participantId = participantId; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public String getCardHolderName() { return cardHolderName; }
    public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}