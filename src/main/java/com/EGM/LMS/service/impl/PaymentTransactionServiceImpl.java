package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.PaymentDTO;
import com.EGM.LMS.dto.PaymentTransactionDTO;
import com.EGM.LMS.model.PaymentTransaction;
import com.EGM.LMS.repository.PaymentRepository;
import com.EGM.LMS.repository.PaymentTransactionRepository;
import com.EGM.LMS.service.PaymentTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentTransactionServiceImpl implements PaymentTransactionService {
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public PaymentTransactionDTO createPaymentTransaction(PaymentTransactionDTO paymentTransaction) {
        return toDto(paymentTransactionRepository.save(toEntity(paymentTransaction)));
    }

    @Override
    public List<PaymentTransactionDTO> getAllPaymentTransactions() {
        var transactions = paymentTransactionRepository.findAll();
        var transactionDtos = new java.util.ArrayList<PaymentTransactionDTO>();
        for (PaymentTransaction transaction : transactions) {
            transactionDtos.add(toDto(transaction));
        }
        return transactionDtos;
    }

    @Override
    public PaymentTransactionDTO getPaymentTransaction(UUID paymentTransactionId) {
        return toDto(paymentTransactionRepository.findById(paymentTransactionId).orElseThrow());
    }

    @Override
    public PaymentTransactionDTO updatePaymentTransaction(UUID paymentTransactionId, PaymentTransactionDTO paymentTransaction) {
        paymentTransactionRepository.findById(paymentTransactionId).orElseThrow();
        var entity = toEntity(paymentTransaction);
        entity.setId(paymentTransactionId);
        return toDto(paymentTransactionRepository.save(entity));
    }

    @Override
    public void deletePaymentTransaction(UUID paymentTransactionId) {
        paymentTransactionRepository.deleteById(paymentTransactionId);
    }

    private PaymentTransaction toEntity(PaymentTransactionDTO paymentTransaction) {
        var paymentId = paymentTransaction.getPayment() != null ? paymentTransaction.getPayment().getId() : null;
        return PaymentTransaction.builder()
                .payment(paymentId != null ? paymentRepository.findById(paymentId).orElse(null) : null)
                .gateway(paymentTransaction.getGateway())
                .transactionType(paymentTransaction.getTransactionType())
                .amount(paymentTransaction.getAmount())
                .currency(paymentTransaction.getCurrency())
                .status(paymentTransaction.getStatus())
                .gatewayRef(paymentTransaction.getGatewayRef())
                .gatewayResponse(paymentTransaction.getGatewayResponse())
                .ipAddress(paymentTransaction.getIpAddress())
                .userAgent(paymentTransaction.getUserAgent())
                .build();
    }

    private PaymentTransactionDTO toDto(PaymentTransaction paymentTransaction) {
        return PaymentTransactionDTO.builder()
                .id(paymentTransaction.getId())
                .payment(paymentTransaction.getPayment() != null ? PaymentDTO.builder().id(paymentTransaction.getPayment().getId()).build() : null)
                .gateway(paymentTransaction.getGateway())
                .transactionType(paymentTransaction.getTransactionType())
                .amount(paymentTransaction.getAmount())
                .currency(paymentTransaction.getCurrency())
                .status(paymentTransaction.getStatus())
                .gatewayRef(paymentTransaction.getGatewayRef())
                .gatewayResponse(paymentTransaction.getGatewayResponse())
                .ipAddress(paymentTransaction.getIpAddress())
                .userAgent(paymentTransaction.getUserAgent())
                .createdAt(paymentTransaction.getCreatedAt())
                .updatedAt(paymentTransaction.getUpdatedAt())
                .build();
    }
}
