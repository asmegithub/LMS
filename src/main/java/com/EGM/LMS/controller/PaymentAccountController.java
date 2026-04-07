package com.EGM.LMS.controller;

import com.EGM.LMS.dto.PaymentAccountDTO;
import com.EGM.LMS.service.PaymentAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment-accounts")
public class PaymentAccountController {
    private final PaymentAccountService paymentAccountService;

    /** Public (student): list active accounts to pay to. */
    @GetMapping("/active")
    ResponseEntity<List<PaymentAccountDTO>> getActive() {
        return ResponseEntity.ok(paymentAccountService.getActive());
    }

    /**
     * Admin: full list / CRUD. (Role enforcement relies on existing security
     * setup.)
     */
    @GetMapping
    @PreAuthorize("hasAuthority('payment-accounts.manage')")
    ResponseEntity<List<PaymentAccountDTO>> getAll() {
        return ResponseEntity.ok(paymentAccountService.getAll());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('payment-accounts.manage')")
    ResponseEntity<PaymentAccountDTO> create(@RequestBody PaymentAccountDTO dto) {
        return ResponseEntity.ok(paymentAccountService.create(dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('payment-accounts.manage')")
    ResponseEntity<PaymentAccountDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentAccountService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('payment-accounts.manage')")
    ResponseEntity<PaymentAccountDTO> update(@PathVariable UUID id, @RequestBody PaymentAccountDTO dto) {
        return ResponseEntity.ok(paymentAccountService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('payment-accounts.manage')")
    ResponseEntity<Void> delete(@PathVariable UUID id) {
        paymentAccountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
