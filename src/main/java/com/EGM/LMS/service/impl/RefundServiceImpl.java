package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.PaymentDTO;
import com.EGM.LMS.dto.RefundDTO;
import com.EGM.LMS.model.Refund;
import com.EGM.LMS.repository.PaymentRepository;
import com.EGM.LMS.repository.RefundRepository;
import com.EGM.LMS.service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {
    private final RefundRepository refundRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public RefundDTO createRefund(RefundDTO refund) {
        return toDto(refundRepository.save(toEntity(refund)));
    }

    @Override
    public List<RefundDTO> getAllRefunds() {
        var refunds = refundRepository.findAll();
        var refundDtos = new java.util.ArrayList<RefundDTO>();
        for (Refund refund : refunds) {
            refundDtos.add(toDto(refund));
        }
        return refundDtos;
    }

    @Override
    public RefundDTO getRefund(UUID refundId) {
        return toDto(refundRepository.findById(refundId).orElseThrow());
    }

    @Override
    public RefundDTO updateRefund(UUID refundId, RefundDTO refund) {
        refundRepository.findById(refundId).orElseThrow();
        var entity = toEntity(refund);
        entity.setId(refundId);
        return toDto(refundRepository.save(entity));
    }

    @Override
    public void deleteRefund(UUID refundId) {
        refundRepository.deleteById(refundId);
    }

    private Refund toEntity(RefundDTO refund) {
        var paymentId = refund.getPayment() != null ? refund.getPayment().getId() : null;
        return Refund.builder()
                .payment(paymentId != null ? paymentRepository.findById(paymentId).orElse(null) : null)
                .amount(refund.getAmount())
                .reason(refund.getReason())
                .status(refund.getStatus())
                .processedBy(refund.getProcessedBy())
                .processedAt(refund.getProcessedAt())
                .gatewayRef(refund.getGatewayRef())
                .notes(refund.getNotes())
                .build();
    }

    private RefundDTO toDto(Refund refund) {
        return RefundDTO.builder()
                .id(refund.getId())
                .payment(refund.getPayment() != null ? PaymentDTO.builder().id(refund.getPayment().getId()).build() : null)
                .amount(refund.getAmount())
                .reason(refund.getReason())
                .status(refund.getStatus())
                .processedBy(refund.getProcessedBy())
                .processedAt(refund.getProcessedAt())
                .gatewayRef(refund.getGatewayRef())
                .notes(refund.getNotes())
                .createdAt(refund.getCreatedAt())
                .updatedAt(refund.getUpdatedAt())
                .build();
    }
}
