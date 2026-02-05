package com.EGM.LMS.service;

import com.EGM.LMS.dto.CouponDTO;

import java.util.List;
import java.util.UUID;

public interface CouponService {
    CouponDTO createCoupon(CouponDTO coupon);
    List<CouponDTO> getAllCoupons();
    CouponDTO getCoupon(UUID couponId);
    CouponDTO updateCoupon(UUID couponId, CouponDTO coupon);
    void deleteCoupon(UUID couponId);
}
