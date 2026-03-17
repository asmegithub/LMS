package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.PayoutMethodOptionDTO;
import com.EGM.LMS.model.PayoutMethodOption;
import com.EGM.LMS.repository.PayoutMethodOptionRepository;
import com.EGM.LMS.service.PayoutMethodOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayoutMethodOptionServiceImpl implements PayoutMethodOptionService {

    private final PayoutMethodOptionRepository repository;

    @Override
    public List<PayoutMethodOptionDTO> getActive() {
        return repository.findByIsActiveTrueOrderByNameAsc().stream().map(this::toDto).toList();
    }

    @Override
    public List<PayoutMethodOptionDTO> getAll() {
        requireAdmin();
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional
    public PayoutMethodOptionDTO create(PayoutMethodOptionDTO dto) {
        requireAdmin();
        if (dto.getName() == null || dto.getName().isBlank()) throw new IllegalArgumentException("name is required");
        if (dto.getType() == null || dto.getType().isBlank()) throw new IllegalArgumentException("type is required");
        var entity = PayoutMethodOption.builder()
                .name(dto.getName())
                .type(dto.getType())
                .fieldsJson(dto.getFieldsJson())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : Boolean.TRUE)
                .build();
        return toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public PayoutMethodOptionDTO update(UUID id, PayoutMethodOptionDTO dto) {
        requireAdmin();
        var existing = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Payout method not found"));
        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getType() != null) existing.setType(dto.getType());
        if (dto.getFieldsJson() != null) existing.setFieldsJson(dto.getFieldsJson());
        if (dto.getIsActive() != null) existing.setIsActive(dto.getIsActive());
        return toDto(repository.save(existing));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        requireAdmin();
        repository.deleteById(id);
    }

    private PayoutMethodOptionDTO toDto(PayoutMethodOption m) {
        return PayoutMethodOptionDTO.builder()
                .id(m.getId())
                .name(m.getName())
                .type(m.getType())
                .fieldsJson(m.getFieldsJson())
                .isActive(m.getIsActive())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .build();
    }

    private void requireAdmin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        // Role enforcement depends on existing security model; we still hard-check when available.
        var roles = auth.getAuthorities();
        boolean isAdmin = roles != null && roles.stream().anyMatch(a -> {
            var v = a.getAuthority();
            return "ADMIN".equalsIgnoreCase(v) || "ROLE_ADMIN".equalsIgnoreCase(v);
        });
        if (!isAdmin) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin only");
    }
}

