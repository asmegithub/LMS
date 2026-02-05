package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.InstructorBankDetailDTO;
import com.EGM.LMS.dto.InstructorProfileDTO;
import com.EGM.LMS.model.InstructorBankDetail;
import com.EGM.LMS.repository.InstructorBankDetailRepository;
import com.EGM.LMS.repository.InstructorProfileRepository;
import com.EGM.LMS.service.InstructorBankDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InstructorBankDetailServiceImpl implements InstructorBankDetailService {
    private final InstructorBankDetailRepository instructorBankDetailRepository;
    private final InstructorProfileRepository instructorProfileRepository;

    @Override
    public InstructorBankDetailDTO createInstructorBankDetail(InstructorBankDetailDTO instructorBankDetail) {
        return toDto(instructorBankDetailRepository.save(toEntity(instructorBankDetail)));
    }

    @Override
    public List<InstructorBankDetailDTO> getAllInstructorBankDetails() {
        var bankDetails = instructorBankDetailRepository.findAll();
        var bankDetailDtos = new java.util.ArrayList<InstructorBankDetailDTO>();
        for (InstructorBankDetail bankDetail : bankDetails) {
            bankDetailDtos.add(toDto(bankDetail));
        }
        return bankDetailDtos;
    }

    @Override
    public InstructorBankDetailDTO getInstructorBankDetail(UUID instructorBankDetailId) {
        return toDto(instructorBankDetailRepository.findById(instructorBankDetailId).orElseThrow());
    }

    @Override
    public InstructorBankDetailDTO updateInstructorBankDetail(UUID instructorBankDetailId, InstructorBankDetailDTO instructorBankDetail) {
        instructorBankDetailRepository.findById(instructorBankDetailId).orElseThrow();
        var entity = toEntity(instructorBankDetail);
        entity.setId(instructorBankDetailId);
        return toDto(instructorBankDetailRepository.save(entity));
    }

    @Override
    public void deleteInstructorBankDetail(UUID instructorBankDetailId) {
        instructorBankDetailRepository.deleteById(instructorBankDetailId);
    }

    private InstructorBankDetail toEntity(InstructorBankDetailDTO instructorBankDetail) {
        var instructorProfileId = instructorBankDetail.getInstructorProfile() != null ? instructorBankDetail.getInstructorProfile().getId() : null;
        return InstructorBankDetail.builder()
                .instructorProfile(instructorProfileId != null ? instructorProfileRepository.findById(instructorProfileId).orElse(null) : null)
                .bankName(instructorBankDetail.getBankName())
                .accountName(instructorBankDetail.getAccountName())
                .accountNumber(instructorBankDetail.getAccountNumber())
                .branchName(instructorBankDetail.getBranchName())
                .swiftCode(instructorBankDetail.getSwiftCode())
                .isPrimary(instructorBankDetail.getIsPrimary())
                .isVerified(instructorBankDetail.getIsVerified())
                .build();
    }

    private InstructorBankDetailDTO toDto(InstructorBankDetail instructorBankDetail) {
        return InstructorBankDetailDTO.builder()
                .id(instructorBankDetail.getId())
                .instructorProfile(instructorBankDetail.getInstructorProfile() != null ? InstructorProfileDTO.builder().id(instructorBankDetail.getInstructorProfile().getId()).build() : null)
                .bankName(instructorBankDetail.getBankName())
                .accountName(instructorBankDetail.getAccountName())
                .accountNumber(instructorBankDetail.getAccountNumber())
                .branchName(instructorBankDetail.getBranchName())
                .swiftCode(instructorBankDetail.getSwiftCode())
                .isPrimary(instructorBankDetail.getIsPrimary())
                .isVerified(instructorBankDetail.getIsVerified())
                .createdAt(instructorBankDetail.getCreatedAt())
                .updatedAt(instructorBankDetail.getUpdatedAt())
                .build();
    }
}
