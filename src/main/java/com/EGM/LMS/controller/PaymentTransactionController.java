package com.EGM.LMS.controller;

import com.EGM.LMS.dto.PaymentTransactionDTO;
import com.EGM.LMS.service.PaymentTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment-transactions")
@PreAuthorize("hasAuthority('payment-transactions.manage')")
public class PaymentTransactionController {
    private final PaymentTransactionService paymentTransactionService;

    @PostMapping
    ResponseEntity<PaymentTransactionDTO> createPaymentTransaction(
            @RequestBody PaymentTransactionDTO paymentTransactionDto) {
        return ResponseEntity.ok(paymentTransactionService.createPaymentTransaction(paymentTransactionDto));
    }

    @GetMapping
    ResponseEntity<List<PaymentTransactionDTO>> getAllPaymentTransactions() {
        return ResponseEntity.ok(paymentTransactionService.getAllPaymentTransactions());
    }

    @GetMapping("/{paymentTransactionId}")
    ResponseEntity<PaymentTransactionDTO> getPaymentTransaction(@PathVariable UUID paymentTransactionId) {
        return ResponseEntity.ok(paymentTransactionService.getPaymentTransaction(paymentTransactionId));
    }

    @PutMapping("/{paymentTransactionId}")
    ResponseEntity<PaymentTransactionDTO> updatePaymentTransaction(@PathVariable UUID paymentTransactionId,
            @RequestBody PaymentTransactionDTO paymentTransactionDto) {
        return ResponseEntity
                .ok(paymentTransactionService.updatePaymentTransaction(paymentTransactionId, paymentTransactionDto));
    }

    @DeleteMapping("/{paymentTransactionId}")
    ResponseEntity<Void> deletePaymentTransaction(@PathVariable UUID paymentTransactionId) {
        paymentTransactionService.deletePaymentTransaction(paymentTransactionId);
        return ResponseEntity.noContent().build();
    }
}
