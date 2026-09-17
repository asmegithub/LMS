package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.SystemSettingDTO;
import com.EGM.LMS.model.SystemSetting;
import com.EGM.LMS.repository.SystemSettingRepository;
import com.EGM.LMS.service.SystemSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SystemSettingServiceImpl implements SystemSettingService {
    private final SystemSettingRepository systemSettingRepository;

    @Override
    public SystemSettingDTO createSystemSetting(SystemSettingDTO systemSetting) {
        return toDto(systemSettingRepository.save(toEntity(systemSetting)));
    }

    @Override
    public List<SystemSettingDTO> getAllSystemSettings() {
        var settings = systemSettingRepository.findAll();
        var settingDtos = new java.util.ArrayList<SystemSettingDTO>();
        for (SystemSetting setting : settings) {
            settingDtos.add(toDto(setting));
        }
        return settingDtos;
    }

    @Override
    public SystemSettingDTO getSystemSetting(UUID systemSettingId) {
        return toDto(systemSettingRepository.findById(systemSettingId).orElseThrow());
    }

    @Override
    public SystemSettingDTO updateSystemSetting(UUID systemSettingId, SystemSettingDTO systemSetting) {
        systemSettingRepository.findById(systemSettingId).orElseThrow();
        var entity = toEntity(systemSetting);
        entity.setId(systemSettingId);
        return toDto(systemSettingRepository.save(entity));
    }

    @Override
    public void deleteSystemSetting(UUID systemSettingId) {
        systemSettingRepository.deleteById(systemSettingId);
    }

    @Override
    public Optional<SystemSettingDTO> getSystemSettingByKey(String key) {
        if (key == null || key.isBlank()) return Optional.empty();
        return systemSettingRepository.findFirstByKey(key)
                .or(() -> systemSettingRepository.findFirstByKeyIgnoreCase(key))
                .map(this::toDto);
    }

    @Override
    public List<SystemSettingDTO> getPublicSystemSettings() {
        return systemSettingRepository.findByIsPublicTrue().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public SystemSettingDTO upsertSystemSetting(SystemSettingDTO dto) {
        if (dto.getKey() == null || dto.getKey().isBlank()) {
            throw new IllegalArgumentException("Setting key is required");
        }
        String trimmedKey = dto.getKey().trim();
        SystemSetting entity = systemSettingRepository.findFirstByKey(trimmedKey)
                .or(() -> systemSettingRepository.findFirstByKeyIgnoreCase(trimmedKey))
                .orElseGet(() -> SystemSetting.builder().key(trimmedKey).build());

        entity.setValue(dto.getValue());
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getIsPublic() != null) {
            entity.setIsPublic(dto.getIsPublic());
        }
        if (dto.getUpdatedBy() != null) {
            entity.setUpdatedBy(dto.getUpdatedBy());
        }

        return toDto(systemSettingRepository.save(entity));
    }

    @Override
    public List<SystemSettingDTO> batchUpsertSystemSettings(List<SystemSettingDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return List.of();
        }
        return dtos.stream()
                .map(this::upsertSystemSetting)
                .toList();
    }

    private SystemSetting toEntity(SystemSettingDTO systemSetting) {
        return SystemSetting.builder()
                .key(systemSetting.getKey())
                .value(systemSetting.getValue())
                .description(systemSetting.getDescription())
                .isPublic(systemSetting.getIsPublic())
                .updatedBy(systemSetting.getUpdatedBy())
                .build();
    }

    private SystemSettingDTO toDto(SystemSetting systemSetting) {
        return SystemSettingDTO.builder()
                .id(systemSetting.getId())
                .key(systemSetting.getKey())
                .value(systemSetting.getValue())
                .description(systemSetting.getDescription())
                .isPublic(systemSetting.getIsPublic())
                .updatedBy(systemSetting.getUpdatedBy())
                .createdAt(systemSetting.getCreatedAt())
                .updatedAt(systemSetting.getUpdatedAt())
                .build();
    }
}
