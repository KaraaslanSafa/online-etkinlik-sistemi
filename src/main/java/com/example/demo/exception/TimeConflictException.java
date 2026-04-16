package com.example.demo.exception;

/**
 * Kullanıcı aynı saatinde iki farklı etkinliğe kaydolmaya çalıştığında fırlatılır
 */
public class TimeConflictException extends RuntimeException {
    
    private final Long eventId;
    private final Long participantId;
    
    public TimeConflictException(Long eventId, Long participantId) {
        super("Zaman çakışması: Katılımcı ID " + participantId + " zaten aynı saatinde başka bir etkinliğe kayıtlıdır.");
        this.eventId = eventId;
        this.participantId = participantId;
    }
    
    public Long getEventId() {
        return eventId;
    }
    
    public Long getParticipantId() {
        return participantId;
    }
}
