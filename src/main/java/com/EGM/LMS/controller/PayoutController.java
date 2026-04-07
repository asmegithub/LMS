package com.EGM.LMS.controller;

import com.EGM.LMS.dto.PayoutDTO;
import com.EGM.LMS.service.PayoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payouts")
@PreAuthorize("hasAuthority('payouts.manage')")
public class PayoutController {
    private final PayoutService payoutService;

    @PostMapping
    ResponseEntity<PayoutDTO> createPayout(@RequestBody PayoutDTO payoutDto) {
        return ResponseEntity.ok(payoutService.createPayout(payoutDto));
    }

    @GetMapping
    ResponseEntity<List<PayoutDTO>> getAllPayouts() {
        return ResponseEntity.ok(payoutService.getAllPayouts());
    }

    @GetMapping("/{payoutId}")
    ResponseEntity<PayoutDTO> getPayout(@PathVariable UUID payoutId) {
        return ResponseEntity.ok(payoutService.getPayout(payoutId));
    }

    @PutMapping("/{payoutId}")
    ResponseEntity<PayoutDTO> updatePayout(@PathVariable UUID payoutId, @RequestBody PayoutDTO payoutDto) {
        return ResponseEntity.ok(payoutService.updatePayout(payoutId, payoutDto));
    }

    @DeleteMapping("/{payoutId}")
    ResponseEntity<Void> deletePayout(@PathVariable UUID payoutId) {
        payoutService.deletePayout(payoutId);
        return ResponseEntity.noContent().build();
    }
}
