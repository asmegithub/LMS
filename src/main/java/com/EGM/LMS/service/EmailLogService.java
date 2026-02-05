package com.EGM.LMS.service;

import com.EGM.LMS.dto.EmailLogDTO;

import java.util.List;
import java.util.UUID;

public interface EmailLogService {
    EmailLogDTO createEmailLog(EmailLogDTO emailLog);
    List<EmailLogDTO> getAllEmailLogs();
    EmailLogDTO getEmailLog(UUID emailLogId);
    EmailLogDTO updateEmailLog(UUID emailLogId, EmailLogDTO emailLog);
    void deleteEmailLog(UUID emailLogId);
}
