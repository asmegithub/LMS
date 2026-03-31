package com.EGM.LMS.service;

import com.EGM.LMS.dto.SystemSettingDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SystemSettingService {
    SystemSettingDTO createSystemSetting(SystemSettingDTO systemSetting);
    List<SystemSettingDTO> getAllSystemSettings();
    SystemSettingDTO getSystemSetting(UUID systemSettingId);
    SystemSettingDTO updateSystemSetting(UUID systemSettingId, SystemSettingDTO systemSetting);
    void deleteSystemSetting(UUID systemSettingId);

    /** Resolve a setting by key (e.g. "PLATFORM_FEE_PERCENT"). */
    Optional<SystemSettingDTO> getSystemSettingByKey(String key);
}
