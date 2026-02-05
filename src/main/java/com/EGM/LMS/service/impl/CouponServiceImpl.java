package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.CouponDTO;
import com.EGM.LMS.model.Coupon;
import com.EGM.LMS.repository.CouponRepository;
import com.EGM.LMS.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;

    @Override
    public CouponDTO createCoupon(CouponDTO coupon) {
        return toDto(couponRepository.save(toEntity(coupon)));
    }

    @Override
    public List<CouponDTO> getAllCoupons() {
        var coupons = couponRepository.findAll();
        var couponDtos = new java.util.ArrayList<CouponDTO>();
        for (Coupon coupon : coupons) {
            couponDtos.add(toDto(coupon));
        }
        return couponDtos;
    }

    @Override
    public CouponDTO getCoupon(UUID couponId) {
        return toDto(couponRepository.findById(couponId).orElseThrow());
    }

    @Override
    public CouponDTO updateCoupon(UUID couponId, CouponDTO coupon) {
        couponRepository.findById(couponId).orElseThrow();
        var entity = toEntity(coupon);
        entity.setId(couponId);
        return toDto(couponRepository.save(entity));
    }

    @Override
    public void deleteCoupon(UUID couponId) {
        couponRepository.deleteById(couponId);
    }

    private Coupon toEntity(CouponDTO coupon) {
        return Coupon.builder()
                .code(coupon.getCode())
                .description(coupon.getDescription())
                .type(coupon.getType())
                .value(coupon.getValue())
                .currency(coupon.getCurrency())
                .minPurchase(coupon.getMinPurchase())
                .maxDiscount(coupon.getMaxDiscount())
                .usageLimit(coupon.getUsageLimit())
                .usageCount(coupon.getUsageCount())
                .perUserLimit(coupon.getPerUserLimit())
                .isActive(coupon.isActive())
                .startDate(coupon.getStartDate())
                .endDate(coupon.getEndDate())
                .build();
    }

    private CouponDTO toDto(Coupon coupon) {
        return CouponDTO.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .description(coupon.getDescription())
                .type(coupon.getType())
                .value(coupon.getValue())
                .currency(coupon.getCurrency())
                .minPurchase(coupon.getMinPurchase())
                .maxDiscount(coupon.getMaxDiscount())
                .usageLimit(coupon.getUsageLimit())
                .usageCount(coupon.getUsageCount())
                .perUserLimit(coupon.getPerUserLimit())
                .isActive(coupon.isActive())
                .startDate(coupon.getStartDate())
                .endDate(coupon.getEndDate())
                .createdAt(coupon.getCreatedAt())
                .updatedAt(coupon.getUpdatedAt())
                .build();
    }
}
