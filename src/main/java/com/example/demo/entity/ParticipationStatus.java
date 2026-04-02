package com.example.demo.entity;

public enum ParticipationStatus {
    REGISTERED("Kayıtlı"),
    ATTENDED("Katıldı"),
    CANCELLED("İptal Edildi"),
    NO_SHOW("Gösterilmedi");
    
    private final String turkishName;
    
    ParticipationStatus(String turkishName) {
        this.turkishName = turkishName;
    }
    
    public String getTurkishName() {
        return turkishName;
    }
}
