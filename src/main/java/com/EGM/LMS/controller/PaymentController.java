package com.EGM.LMS.controller;

import com.EGM.LMS.dto.PaymentDTO;
import com.EGM.LMS.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    ResponseEntity<PaymentDTO> createPayment(@RequestBody PaymentDTO paymentDto) {
        return ResponseEntity.ok(paymentService.createPayment(paymentDto));
    }

    @GetMapping
    ResponseEntity<List<PaymentDTO>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/{paymentId}")
    ResponseEntity<PaymentDTO> getPayment(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(paymentService.getPayment(paymentId));
    }

    @PutMapping("/{paymentId}")
    ResponseEntity<PaymentDTO> updatePayment(@PathVariable UUID paymentId, @RequestBody PaymentDTO paymentDto) {
        return ResponseEntity.ok(paymentService.updatePayment(paymentId, paymentDto));
    }

    @DeleteMapping("/{paymentId}")
    ResponseEntity<Void> deletePayment(@PathVariable UUID paymentId) {
        paymentService.deletePayment(paymentId);
        return ResponseEntity.noContent().build();
    }
}
