package com.example.demo.exception;

public class DuplicateRegistrationException extends RuntimeException {
    public DuplicateRegistrationException(String message) {
        super(message);
    }
    
    public DuplicateRegistrationException(Long eventId, Long participantId) {
        super("Katılımcı zaten bu etkinliğe kayıtlı. Event: " + eventId + ", Participant: " + participantId);
    }
}
