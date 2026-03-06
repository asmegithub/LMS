package com.EGM.LMS.controller;

import com.EGM.LMS.dto.WishlistDTO;
import com.EGM.LMS.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wishlists")
public class WishlistController {
    private final WishlistService wishlistService;

    @PostMapping
    ResponseEntity<WishlistDTO> createWishlist(@RequestBody WishlistDTO wishlistDto) {
        return ResponseEntity.ok(wishlistService.createWishlist(wishlistDto));
    }

    @GetMapping
    ResponseEntity<List<WishlistDTO>> getAllWishlists() {
        return ResponseEntity.ok(wishlistService.getAllWishlists());
    }

    @GetMapping("/me")
    ResponseEntity<List<WishlistDTO>> getMyWishlists() {
        return ResponseEntity.ok(wishlistService.getMyWishlists());
    }

    @PostMapping("/add")
    ResponseEntity<WishlistDTO> addCourse(@RequestParam UUID courseId) {
        return ResponseEntity.ok(wishlistService.addToWishlist(courseId));
    }

    @DeleteMapping("/remove")
    ResponseEntity<Void> removeCourse(@RequestParam UUID courseId) {
        wishlistService.removeFromWishlist(courseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check")
    ResponseEntity<Boolean> checkInWishlist(@RequestParam UUID courseId) {
        return ResponseEntity.ok(wishlistService.isInWishlist(courseId));
    }

    @GetMapping("/{wishlistId}")
    ResponseEntity<WishlistDTO> getWishlist(@PathVariable UUID wishlistId) {
        return ResponseEntity.ok(wishlistService.getWishlist(wishlistId));
    }

    @PutMapping("/{wishlistId}")
    ResponseEntity<WishlistDTO> updateWishlist(@PathVariable UUID wishlistId, @RequestBody WishlistDTO wishlistDto) {
        return ResponseEntity.ok(wishlistService.updateWishlist(wishlistId, wishlistDto));
    }

    @DeleteMapping("/{wishlistId}")
    ResponseEntity<Void> deleteWishlist(@PathVariable UUID wishlistId) {
        wishlistService.deleteWishlist(wishlistId);
        return ResponseEntity.noContent().build();
    }
}
