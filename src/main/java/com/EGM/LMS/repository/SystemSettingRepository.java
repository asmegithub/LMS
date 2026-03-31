package com.EGM.LMS.repository;

import com.EGM.LMS.model.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.Optional;

public interface SystemSettingRepository extends JpaRepository<SystemSetting, UUID> {
    Optional<SystemSetting> findFirstByKey(String key);
}
