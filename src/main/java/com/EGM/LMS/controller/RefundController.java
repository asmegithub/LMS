package com.EGM.LMS.controller;

import com.EGM.LMS.dto.RefundDTO;
import com.EGM.LMS.service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/refunds")
public class RefundController {
    private final RefundService refundService;

    @PostMapping
    ResponseEntity<RefundDTO> createRefund(@RequestBody RefundDTO refundDto) {
        return ResponseEntity.ok(refundService.createRefund(refundDto));
    }

    @GetMapping
    ResponseEntity<List<RefundDTO>> getAllRefunds() {
        return ResponseEntity.ok(refundService.getAllRefunds());
    }

    @GetMapping("/{refundId}")
    ResponseEntity<RefundDTO> getRefund(@PathVariable UUID refundId) {
        return ResponseEntity.ok(refundService.getRefund(refundId));
    }

    @PutMapping("/{refundId}")
    ResponseEntity<RefundDTO> updateRefund(@PathVariable UUID refundId, @RequestBody RefundDTO refundDto) {
        return ResponseEntity.ok(refundService.updateRefund(refundId, refundDto));
    }

    @DeleteMapping("/{refundId}")
    ResponseEntity<Void> deleteRefund(@PathVariable UUID refundId) {
        refundService.deleteRefund(refundId);
        return ResponseEntity.noContent().build();
    }
}
