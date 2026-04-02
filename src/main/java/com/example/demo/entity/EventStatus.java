package com.example.demo.entity;

public enum EventStatus {
    PLANNED("Planlanan"),
    ONGOING("Devam Ediyor"),
    COMPLETED("Tamamlandı"),
    CANCELLED("İptal Edildi");
    
    private final String turkishName;
    
    EventStatus(String turkishName) {
        this.turkishName = turkishName;
    }
    
    public String getTurkishName() {
        return turkishName;
    }
}
