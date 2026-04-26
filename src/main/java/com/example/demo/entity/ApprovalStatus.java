package com.example.demo.entity;

public enum ApprovalStatus {
    PENDING("Onay Beklemede"),
    APPROVED("Onaylandı"),
    REJECTED("Reddedildi");
    
    private final String turkishName;
    
    ApprovalStatus(String turkishName) {
        this.turkishName = turkishName;
    }
    
    public String getTurkishName() {
        return turkishName;
    }
}
