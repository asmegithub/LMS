package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.ReferralDTO;
import com.EGM.LMS.dto.ReferralRewardDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.ReferralReward;
import com.EGM.LMS.repository.ReferralRepository;
import com.EGM.LMS.repository.ReferralRewardRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.ReferralRewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReferralRewardServiceImpl implements ReferralRewardService {
    private final ReferralRewardRepository referralRewardRepository;
    private final ReferralRepository referralRepository;
    private final UserRepository userRepository;

    @Override
    public ReferralRewardDTO createReferralReward(ReferralRewardDTO referralReward) {
        return toDto(referralRewardRepository.save(toEntity(referralReward)));
    }

    @Override
    public List<ReferralRewardDTO> getAllReferralRewards() {
        var rewards = referralRewardRepository.findAll();
        var rewardDtos = new java.util.ArrayList<ReferralRewardDTO>();
        for (ReferralReward reward : rewards) {
            rewardDtos.add(toDto(reward));
        }
        return rewardDtos;
    }

    @Override
    public ReferralRewardDTO getReferralReward(UUID referralRewardId) {
        return toDto(referralRewardRepository.findById(referralRewardId).orElseThrow());
    }

    @Override
    public ReferralRewardDTO updateReferralReward(UUID referralRewardId, ReferralRewardDTO referralReward) {
        referralRewardRepository.findById(referralRewardId).orElseThrow();
        var entity = toEntity(referralReward);
        entity.setId(referralRewardId);
        return toDto(referralRewardRepository.save(entity));
    }

    @Override
    public void deleteReferralReward(UUID referralRewardId) {
        referralRewardRepository.deleteById(referralRewardId);
    }

    private ReferralReward toEntity(ReferralRewardDTO referralReward) {
        var referralId = referralReward.getReferral() != null ? referralReward.getReferral().getId() : null;
        var userId = referralReward.getUser() != null ? referralReward.getUser().getId() : null;
        return ReferralReward.builder()
                .referral(referralId != null ? referralRepository.findById(referralId).orElse(null) : null)
                .user(userId != null ? userRepository.findById(userId).orElse(null) : null)
                .amount(referralReward.getAmount())
                .type(referralReward.getType())
                .description(referralReward.getDescription())
                .isPaidOut(referralReward.isPaidOut())
                .paidOutAt(referralReward.getPaidOutAt())
                .build();
    }

    private ReferralRewardDTO toDto(ReferralReward referralReward) {
        return ReferralRewardDTO.builder()
                .id(referralReward.getId())
                .referral(referralReward.getReferral() != null ? ReferralDTO.builder().id(referralReward.getReferral().getId()).build() : null)
                .user(referralReward.getUser() != null ? UserDTO.builder().id(referralReward.getUser().getId()).build() : null)
                .amount(referralReward.getAmount())
                .type(referralReward.getType())
                .description(referralReward.getDescription())
                .isPaidOut(referralReward.isPaidOut())
                .paidOutAt(referralReward.getPaidOutAt())
                .createdAt(referralReward.getCreatedAt())
                .updatedAt(referralReward.getUpdatedAt())
                .build();
    }
}
