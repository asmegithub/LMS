package com.EGM.LMS.service;

import com.EGM.LMS.dto.ReviewDTO;

import java.util.List;
import java.util.UUID;

public interface ReviewService {
    ReviewDTO createReview(ReviewDTO review);
    List<ReviewDTO> getAllReviews();
    ReviewDTO getReview(UUID reviewId);
    ReviewDTO updateReview(UUID reviewId, ReviewDTO review);
    void deleteReview(UUID reviewId);
}
