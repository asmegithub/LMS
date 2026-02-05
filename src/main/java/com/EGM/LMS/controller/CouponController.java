package com.EGM.LMS.controller;

import com.EGM.LMS.dto.CouponDTO;
import com.EGM.LMS.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
public class CouponController {
    private final CouponService couponService;

    @PostMapping
    ResponseEntity<CouponDTO> createCoupon(@RequestBody CouponDTO couponDto) {
        return ResponseEntity.ok(couponService.createCoupon(couponDto));
    }

    @GetMapping
    ResponseEntity<List<CouponDTO>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    @GetMapping("/{couponId}")
    ResponseEntity<CouponDTO> getCoupon(@PathVariable UUID couponId) {
        return ResponseEntity.ok(couponService.getCoupon(couponId));
    }

    @PutMapping("/{couponId}")
    ResponseEntity<CouponDTO> updateCoupon(@PathVariable UUID couponId, @RequestBody CouponDTO couponDto) {
        return ResponseEntity.ok(couponService.updateCoupon(couponId, couponDto));
    }

    @DeleteMapping("/{couponId}")
    ResponseEntity<Void> deleteCoupon(@PathVariable UUID couponId) {
        couponService.deleteCoupon(couponId);
        return ResponseEntity.noContent().build();
    }
}
