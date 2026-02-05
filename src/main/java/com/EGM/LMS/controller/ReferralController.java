package com.EGM.LMS.controller;

import com.EGM.LMS.dto.ReferralDTO;
import com.EGM.LMS.service.ReferralService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/referrals")
public class ReferralController {
    private final ReferralService referralService;

    @PostMapping
    ResponseEntity<ReferralDTO> createReferral(@RequestBody ReferralDTO referralDto) {
        return ResponseEntity.ok(referralService.createReferral(referralDto));
    }

    @GetMapping
    ResponseEntity<List<ReferralDTO>> getAllReferrals() {
        return ResponseEntity.ok(referralService.getAllReferrals());
    }

    @GetMapping("/{referralId}")
    ResponseEntity<ReferralDTO> getReferral(@PathVariable UUID referralId) {
        return ResponseEntity.ok(referralService.getReferral(referralId));
    }

    @PutMapping("/{referralId}")
    ResponseEntity<ReferralDTO> updateReferral(@PathVariable UUID referralId, @RequestBody ReferralDTO referralDto) {
        return ResponseEntity.ok(referralService.updateReferral(referralId, referralDto));
    }

    @DeleteMapping("/{referralId}")
    ResponseEntity<Void> deleteReferral(@PathVariable UUID referralId) {
        referralService.deleteReferral(referralId);
        return ResponseEntity.noContent().build();
    }
}
