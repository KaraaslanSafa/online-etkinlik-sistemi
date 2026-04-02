package com.example.demo.exception;

public class EventFullException extends RuntimeException {
    public EventFullException(String message) {
        super(message);
    }
    
    public EventFullException(String eventTitle, int capacity) {
        super("Etkinlik '" + eventTitle + "' dolu (Kapasite: " + capacity + ")");
    }
}
