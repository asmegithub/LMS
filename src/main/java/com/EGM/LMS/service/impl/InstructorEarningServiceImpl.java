package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.InstructorEarningDTO;
import com.EGM.LMS.dto.InstructorProfileDTO;
import com.EGM.LMS.model.InstructorEarning;
import com.EGM.LMS.repository.InstructorEarningRepository;
import com.EGM.LMS.repository.InstructorProfileRepository;
import com.EGM.LMS.service.InstructorEarningService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InstructorEarningServiceImpl implements InstructorEarningService {
    private final InstructorEarningRepository instructorEarningRepository;
    private final InstructorProfileRepository instructorProfileRepository;

    @Override
    public Optional<InstructorEarningDTO> getMyEarning() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) return Optional.empty();
        var profile = instructorProfileRepository.findFirstByUser_Email(auth.getName());
        if (profile.isEmpty()) return Optional.empty();
        return instructorEarningRepository.findFirstByInstructorProfile_Id(profile.get().getId()).map(this::toDto);
    }

    @Override
    public InstructorEarningDTO createInstructorEarning(InstructorEarningDTO instructorEarning) {
        return toDto(instructorEarningRepository.save(toEntity(instructorEarning)));
    }

    @Override
    public List<InstructorEarningDTO> getAllInstructorEarnings() {
        var earnings = instructorEarningRepository.findAll();
        var earningDtos = new java.util.ArrayList<InstructorEarningDTO>();
        for (InstructorEarning earning : earnings) {
            earningDtos.add(toDto(earning));
        }
        return earningDtos;
    }

    @Override
    public InstructorEarningDTO getInstructorEarning(UUID instructorEarningId) {
        return toDto(instructorEarningRepository.findById(instructorEarningId).orElseThrow());
    }

    @Override
    public InstructorEarningDTO updateInstructorEarning(UUID instructorEarningId, InstructorEarningDTO instructorEarning) {
        instructorEarningRepository.findById(instructorEarningId).orElseThrow();
        var entity = toEntity(instructorEarning);
        entity.setId(instructorEarningId);
        return toDto(instructorEarningRepository.save(entity));
    }

    @Override
    public void deleteInstructorEarning(UUID instructorEarningId) {
        instructorEarningRepository.deleteById(instructorEarningId);
    }

    private InstructorEarning toEntity(InstructorEarningDTO instructorEarning) {
        var instructorProfileId = instructorEarning.getInstructorProfile() != null ? instructorEarning.getInstructorProfile().getId() : null;
        return InstructorEarning.builder()
                .instructorProfile(instructorProfileId != null ? instructorProfileRepository.findById(instructorProfileId).orElse(null) : null)
                .totalEarnings(instructorEarning.getTotalEarnings())
                .totalWithdrawn(instructorEarning.getTotalWithdrawn())
                .currentBalance(instructorEarning.getCurrentBalance())
                .lastMonthEarning(instructorEarning.getLastMonthEarning())
                .lastWithdrawnAt(instructorEarning.getLastWithdrawnAt())
                .build();
    }

    private InstructorEarningDTO toDto(InstructorEarning instructorEarning) {
        return InstructorEarningDTO.builder()
                .id(instructorEarning.getId())
                .instructorProfile(instructorEarning.getInstructorProfile() != null ? InstructorProfileDTO.builder().id(instructorEarning.getInstructorProfile().getId()).build() : null)
                .totalEarnings(instructorEarning.getTotalEarnings())
                .totalWithdrawn(instructorEarning.getTotalWithdrawn())
                .currentBalance(instructorEarning.getCurrentBalance())
                .lastMonthEarning(instructorEarning.getLastMonthEarning())
                .lastWithdrawnAt(instructorEarning.getLastWithdrawnAt())
                .createdAt(instructorEarning.getCreatedAt())
                .updatedAt(instructorEarning.getUpdatedAt())
                .build();
    }
}
