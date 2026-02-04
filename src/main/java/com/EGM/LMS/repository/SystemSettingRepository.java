package com.EGM.LMS.repository;

import com.EGM.LMS.model.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SystemSettingRepository extends JpaRepository<SystemSetting, UUID> {
}
