package com.EGM.LMS.service;

import com.EGM.LMS.dto.AuditLogDTO;

import java.util.List;
import java.util.UUID;

public interface AuditLogService {
    AuditLogDTO createAuditLog(AuditLogDTO auditLog);
    /** Create an audit log for the currently authenticated admin. */
    void logAction(String action, String targetType, String targetId, String changes, String ipAddress, String userAgent);
    List<AuditLogDTO> getAllAuditLogs();
    AuditLogDTO getAuditLog(UUID auditLogId);
    AuditLogDTO updateAuditLog(UUID auditLogId, AuditLogDTO auditLog);
    void deleteAuditLog(UUID auditLogId);
}
