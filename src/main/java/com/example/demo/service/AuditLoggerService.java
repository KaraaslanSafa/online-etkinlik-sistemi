package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.entity.AuditLog;
import com.example.demo.repository.AuditLogRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuditLoggerService {

    private final AuditLogRepository auditLogRepository;

    public AuditLoggerService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logAction(Long userId, String action, String entityType, Long entityId, String details) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setUserId(userId);
            auditLog.setAction(action);
            auditLog.setEntityType(entityType);
            auditLog.setEntityId(entityId);
            auditLog.setDetails(details);
            
            auditLogRepository.save(auditLog);
            log.info("Audit Log Kaydedildi: Kullanıcı: {}, İşlem: {}, Entity: {}, ID: {}", 
                    userId, action, entityType, entityId);
        } catch (Exception e) {
            log.error("Audit log kaydedilemedi: {}", e.getMessage());
        }
    }
}
