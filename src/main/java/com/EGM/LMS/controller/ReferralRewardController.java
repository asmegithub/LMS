package com.EGM.LMS.controller;

import com.EGM.LMS.dto.ReferralRewardDTO;
import com.EGM.LMS.service.ReferralRewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/referral-rewards")
public class ReferralRewardController {
    private final ReferralRewardService referralRewardService;

    @PostMapping
    ResponseEntity<ReferralRewardDTO> createReferralReward(@RequestBody ReferralRewardDTO referralRewardDto) {
        return ResponseEntity.ok(referralRewardService.createReferralReward(referralRewardDto));
    }

    @GetMapping
    ResponseEntity<List<ReferralRewardDTO>> getAllReferralRewards() {
        return ResponseEntity.ok(referralRewardService.getAllReferralRewards());
    }

    @GetMapping("/{referralRewardId}")
    ResponseEntity<ReferralRewardDTO> getReferralReward(@PathVariable UUID referralRewardId) {
        return ResponseEntity.ok(referralRewardService.getReferralReward(referralRewardId));
    }

    @PutMapping("/{referralRewardId}")
    ResponseEntity<ReferralRewardDTO> updateReferralReward(@PathVariable UUID referralRewardId, @RequestBody ReferralRewardDTO referralRewardDto) {
        return ResponseEntity.ok(referralRewardService.updateReferralReward(referralRewardId, referralRewardDto));
    }

    @DeleteMapping("/{referralRewardId}")
    ResponseEntity<Void> deleteReferralReward(@PathVariable UUID referralRewardId) {
        referralRewardService.deleteReferralReward(referralRewardId);
        return ResponseEntity.noContent().build();
    }
}
