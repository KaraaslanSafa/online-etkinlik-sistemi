package com.example.demo.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static ResourceNotFoundException eventNotFound(Long id) {
        return new ResourceNotFoundException("Etkinlik bulunamadı: " + id);
    }
    
    public static ResourceNotFoundException categoryNotFound(Long id) {
        return new ResourceNotFoundException("Kategori bulunamadı: " + id);
    }
    
    public static ResourceNotFoundException participantNotFound(Long id) {
        return new ResourceNotFoundException("Katılımcı bulunamadı: " + id);
    }
    
    public static ResourceNotFoundException participantNotRegistered(Long eventId, Long participantId) {
        return new ResourceNotFoundException("Katılımcı etkinliğe kayıtlı değil. Event: " + eventId + ", Participant: " + participantId);
    }
}
