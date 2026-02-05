package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.InstructorProfileDTO;
import com.EGM.LMS.dto.PayoutDTO;
import com.EGM.LMS.model.Payout;
import com.EGM.LMS.repository.InstructorProfileRepository;
import com.EGM.LMS.repository.PayoutRepository;
import com.EGM.LMS.service.PayoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayoutServiceImpl implements PayoutService {
    private final PayoutRepository payoutRepository;
    private final InstructorProfileRepository instructorProfileRepository;

    @Override
    public PayoutDTO createPayout(PayoutDTO payout) {
        return toDto(payoutRepository.save(toEntity(payout)));
    }

    @Override
    public List<PayoutDTO> getAllPayouts() {
        var payouts = payoutRepository.findAll();
        var payoutDtos = new java.util.ArrayList<PayoutDTO>();
        for (Payout payout : payouts) {
            payoutDtos.add(toDto(payout));
        }
        return payoutDtos;
    }

    @Override
    public PayoutDTO getPayout(UUID payoutId) {
        return toDto(payoutRepository.findById(payoutId).orElseThrow());
    }

    @Override
    public PayoutDTO updatePayout(UUID payoutId, PayoutDTO payout) {
        payoutRepository.findById(payoutId).orElseThrow();
        var entity = toEntity(payout);
        entity.setId(payoutId);
        return toDto(payoutRepository.save(entity));
    }

    @Override
    public void deletePayout(UUID payoutId) {
        payoutRepository.deleteById(payoutId);
    }

    private Payout toEntity(PayoutDTO payout) {
        var instructorProfileId = payout.getInstructorProfile() != null ? payout.getInstructorProfile().getId() : null;
        return Payout.builder()
                .instructorProfile(instructorProfileId != null ? instructorProfileRepository.findById(instructorProfileId).orElse(null) : null)
                .amount(payout.getAmount())
                .currency(payout.getCurrency())
                .status(payout.getStatus())
                .paymentMethod(payout.getPaymentMethod())
                .paymentDetails(payout.getPaymentDetails())
                .referenceId(payout.getReferenceId())
                .failureReason(payout.getFailureReason())
                .requestedAt(payout.getRequestedAt())
                .processedAt(payout.getProcessedAt())
                .build();
    }

    private PayoutDTO toDto(Payout payout) {
        return PayoutDTO.builder()
                .id(payout.getId())
                .instructorProfile(payout.getInstructorProfile() != null ? InstructorProfileDTO.builder().id(payout.getInstructorProfile().getId()).build() : null)
                .amount(payout.getAmount())
                .currency(payout.getCurrency())
                .status(payout.getStatus())
                .paymentMethod(payout.getPaymentMethod())
                .paymentDetails(payout.getPaymentDetails())
                .referenceId(payout.getReferenceId())
                .failureReason(payout.getFailureReason())
                .requestedAt(payout.getRequestedAt())
                .processedAt(payout.getProcessedAt())
                .createdAt(payout.getCreatedAt())
                .updatedAt(payout.getUpdatedAt())
                .build();
    }
}
