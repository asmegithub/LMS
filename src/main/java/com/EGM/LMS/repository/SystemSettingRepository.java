package com.EGM.LMS.repository;

import com.EGM.LMS.model.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SystemSettingRepository extends JpaRepository<SystemSetting, UUID> {
    Optional<SystemSetting> findFirstByKey(String key);
    Optional<SystemSetting> findFirstByKeyIgnoreCase(String key);
    List<SystemSetting> findByIsPublicTrue();
}
