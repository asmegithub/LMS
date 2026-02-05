package com.EGM.LMS.controller;

import com.EGM.LMS.dto.ReviewDTO;
import com.EGM.LMS.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO reviewDto) {
        return ResponseEntity.ok(reviewService.createReview(reviewDto));
    }

    @GetMapping
    ResponseEntity<List<ReviewDTO>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @GetMapping("/{reviewId}")
    ResponseEntity<ReviewDTO> getReview(@PathVariable UUID reviewId) {
        return ResponseEntity.ok(reviewService.getReview(reviewId));
    }

    @PutMapping("/{reviewId}")
    ResponseEntity<ReviewDTO> updateReview(@PathVariable UUID reviewId, @RequestBody ReviewDTO reviewDto) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, reviewDto));
    }

    @DeleteMapping("/{reviewId}")
    ResponseEntity<Void> deleteReview(@PathVariable UUID reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
