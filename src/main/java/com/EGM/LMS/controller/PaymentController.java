package com.EGM.LMS.controller;

import com.EGM.LMS.dto.ChapaInitializeRequest;
import com.EGM.LMS.dto.ChapaInitializeResponse;
import com.EGM.LMS.dto.PaymentDTO;
import com.EGM.LMS.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('payments.manage')")
    ResponseEntity<PaymentDTO> createPayment(@RequestBody PaymentDTO paymentDto) {
        return ResponseEntity.ok(paymentService.createPayment(paymentDto));
    }

    @PostMapping("/chapa/initialize")
    @PreAuthorize("isAuthenticated()")
    ResponseEntity<ChapaInitializeResponse> chapaInitialize(@RequestBody ChapaInitializeRequest request) {
        return ResponseEntity.ok(paymentService.initializeChapaPayment(request));
    }

    @GetMapping("/chapa/callback")
    ResponseEntity<Void> chapaCallback(
            @RequestParam(name = "trx_ref", required = false) String trxRef,
            @RequestParam(name = "ref_id", required = false) String refId,
            @RequestParam(name = "status", required = false) String status) {
        paymentService.handleChapaCallback(trxRef, refId, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('payments.manage')")
    ResponseEntity<List<PaymentDTO>> getPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/me")
    ResponseEntity<List<PaymentDTO>> getMyPayments() {
        return ResponseEntity.ok(paymentService.getMyPayments());
    }

    @GetMapping("/{paymentId}")
    @PreAuthorize("hasAuthority('payments.manage')")
    ResponseEntity<PaymentDTO> getPayment(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(paymentService.getPayment(paymentId));
    }

    @PutMapping("/{paymentId}")
    @PreAuthorize("hasAuthority('payments.manage')")
    ResponseEntity<PaymentDTO> updatePayment(@PathVariable UUID paymentId, @RequestBody PaymentDTO paymentDto) {
        return ResponseEntity.ok(paymentService.updatePayment(paymentId, paymentDto));
    }

    @DeleteMapping("/{paymentId}")
    @PreAuthorize("hasAuthority('payments.manage')")
    ResponseEntity<Void> deletePayment(@PathVariable UUID paymentId) {
        paymentService.deletePayment(paymentId);
        return ResponseEntity.noContent().build();
    }
}
