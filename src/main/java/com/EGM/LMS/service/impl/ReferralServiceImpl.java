package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.ReferralDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.Referral;
import com.EGM.LMS.repository.ReferralRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.ReferralService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReferralServiceImpl implements ReferralService {
    private final ReferralRepository referralRepository;
    private final UserRepository userRepository;

    @Override
    public ReferralDTO createReferral(ReferralDTO referral) {
        return toDto(referralRepository.save(toEntity(referral)));
    }

    @Override
    public List<ReferralDTO> getAllReferrals() {
        var referrals = referralRepository.findAll();
        var referralDtos = new java.util.ArrayList<ReferralDTO>();
        for (Referral referral : referrals) {
            referralDtos.add(toDto(referral));
        }
        return referralDtos;
    }

    @Override
    public ReferralDTO getReferral(UUID referralId) {
        return toDto(referralRepository.findById(referralId).orElseThrow());
    }

    @Override
    public ReferralDTO updateReferral(UUID referralId, ReferralDTO referral) {
        referralRepository.findById(referralId).orElseThrow();
        var entity = toEntity(referral);
        entity.setId(referralId);
        return toDto(referralRepository.save(entity));
    }

    @Override
    public void deleteReferral(UUID referralId) {
        referralRepository.deleteById(referralId);
    }

    private Referral toEntity(ReferralDTO referral) {
        var referrerId = referral.getReferrer() != null ? referral.getReferrer().getId() : null;
        var refereeId = referral.getReferee() != null ? referral.getReferee().getId() : null;
        return Referral.builder()
                .referrer(referrerId != null ? userRepository.findById(referrerId).orElse(null) : null)
                .referee(refereeId != null ? userRepository.findById(refereeId).orElse(null) : null)
                .referralCode(referral.getReferralCode())
                .status(referral.getStatus())
                .rewardAmount(referral.getRewardAmount())
                .rewardGiven(referral.getRewardGiven())
                .referredUserPurchased(referral.getReferredUserPurchased())
                .rewardedAt(referral.getRewardedAt())
                .build();
    }

    private ReferralDTO toDto(Referral referral) {
        return ReferralDTO.builder()
                .id(referral.getId())
                .referrer(referral.getReferrer() != null ? UserDTO.builder().id(referral.getReferrer().getId()).build() : null)
                .referee(referral.getReferee() != null ? UserDTO.builder().id(referral.getReferee().getId()).build() : null)
                .referralCode(referral.getReferralCode())
                .status(referral.getStatus())
                .rewardAmount(referral.getRewardAmount())
                .rewardGiven(referral.getRewardGiven())
                .referredUserPurchased(referral.getReferredUserPurchased())
                .rewardedAt(referral.getRewardedAt())
                .createdAt(referral.getCreatedAt())
                .updatedAt(referral.getUpdatedAt())
                .build();
    }
}
