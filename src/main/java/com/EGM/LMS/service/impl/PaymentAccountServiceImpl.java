package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.PaymentAccountDTO;
import com.EGM.LMS.model.PaymentAccount;
import com.EGM.LMS.repository.PaymentAccountRepository;
import com.EGM.LMS.service.PaymentAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentAccountServiceImpl implements PaymentAccountService {
    private final PaymentAccountRepository paymentAccountRepository;

    @Override
    public PaymentAccountDTO create(PaymentAccountDTO dto) {
        return toDto(paymentAccountRepository.save(toEntity(dto)));
    }

    @Override
    public List<PaymentAccountDTO> getAll() {
        return paymentAccountRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public List<PaymentAccountDTO> getActive() {
        return paymentAccountRepository.findByIsActiveTrueOrderByProviderNameAsc().stream().map(this::toDto).toList();
    }

    @Override
    public PaymentAccountDTO getById(UUID id) {
        return toDto(paymentAccountRepository.findById(id).orElseThrow());
    }

    @Override
    public PaymentAccountDTO update(UUID id, PaymentAccountDTO dto) {
        paymentAccountRepository.findById(id).orElseThrow();
        var entity = toEntity(dto);
        entity.setId(id);
        return toDto(paymentAccountRepository.save(entity));
    }

    @Override
    public void delete(UUID id) {
        paymentAccountRepository.deleteById(id);
    }

    private PaymentAccount toEntity(PaymentAccountDTO dto) {
        return PaymentAccount.builder()
                .providerName(dto.getProviderName())
                .type(dto.getType() != null ? dto.getType() : "BANK")
                .accountName(dto.getAccountName())
                .accountNumber(dto.getAccountNumber())
                .ussdCode(dto.getUssdCode())
                .instructions(dto.getInstructions())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : Boolean.TRUE)
                .build();
    }

    private PaymentAccountDTO toDto(PaymentAccount a) {
        return PaymentAccountDTO.builder()
                .id(a.getId())
                .providerName(a.getProviderName())
                .type(a.getType())
                .accountName(a.getAccountName())
                .accountNumber(a.getAccountNumber())
                .ussdCode(a.getUssdCode())
                .instructions(a.getInstructions())
                .isActive(a.getIsActive())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }
}

