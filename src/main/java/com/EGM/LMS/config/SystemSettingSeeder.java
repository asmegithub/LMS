package com.EGM.LMS.config;

import com.EGM.LMS.model.SystemSetting;
import com.EGM.LMS.repository.SystemSettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds default system settings (like site name, tagline, fee percentages) if not already present.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Order(1500)
public class SystemSettingSeeder implements ApplicationRunner {

    private final SystemSettingRepository systemSettingRepository;

    @Override
    public void run(ApplicationArguments args) {
        seedIfMissing("SITE_NAME", "BeteGubae", "Platform project and brand name", true);
        seedIfMissing("SITE_TAGLINE", "Ethiopia's Premier Learning Platform", "Platform tagline", true);
        seedIfMissing("SUPPORT_EMAIL", "support@BeteGubae.et", "Official support email address", true);
        seedIfMissing("DEFAULT_LANGUAGE", "en", "Default system language", true);
        seedIfMissing("MAINTENANCE_MODE", "false", "Maintenance mode toggle", true);
        seedIfMissing("REGISTRATION_OPEN", "true", "New user registration status", true);
        seedIfMissing("REQUIRE_EMAIL_VERIFICATION", "true", "Require verified email to purchase/learn", true);
        seedIfMissing("PLATFORM_FEE_PERCENT", "15", "Platform fee percentage applied to course payments", false);
        seedIfMissing("REFERRAL_REWARD_PERCENT", "5", "Referral reward percentage for successful enrollments", false);
    }

    private void seedIfMissing(String key, String value, String description, boolean isPublic) {
        if (systemSettingRepository.findFirstByKey(key).isEmpty() &&
            systemSettingRepository.findFirstByKeyIgnoreCase(key).isEmpty()) {
            SystemSetting setting = SystemSetting.builder()
                    .key(key)
                    .value(value)
                    .description(description)
                    .isPublic(isPublic)
                    .updatedBy("SYSTEM_SEEDER")
                    .build();
            systemSettingRepository.save(setting);
            log.info("[SystemSettingSeeder] Seeded default setting '{}' = '{}'", key, value);
        }
    }
}
