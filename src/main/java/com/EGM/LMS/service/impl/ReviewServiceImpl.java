package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.CourseDTO;
import com.EGM.LMS.dto.ReviewDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.Review;
import com.EGM.LMS.repository.CourseRepository;
import com.EGM.LMS.repository.ReviewRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Override
    public ReviewDTO createReview(ReviewDTO review) {
        return toDto(reviewRepository.save(toEntity(review)));
    }

    @Override
    public List<ReviewDTO> getAllReviews() {
        var reviews = reviewRepository.findAll();
        var reviewDtos = new java.util.ArrayList<ReviewDTO>();
        for (Review review : reviews) {
            reviewDtos.add(toDto(review));
        }
        return reviewDtos;
    }

    @Override
    public ReviewDTO getReview(UUID reviewId) {
        return toDto(reviewRepository.findById(reviewId).orElseThrow());
    }

    @Override
    public ReviewDTO updateReview(UUID reviewId, ReviewDTO review) {
        reviewRepository.findById(reviewId).orElseThrow();
        var entity = toEntity(review);
        entity.setId(reviewId);
        return toDto(reviewRepository.save(entity));
    }

    @Override
    public void deleteReview(UUID reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    private Review toEntity(ReviewDTO review) {
        var courseId = review.getCourse() != null ? review.getCourse().getId() : null;
        var studentId = review.getStudent() != null ? review.getStudent().getId() : null;
        return Review.builder()
                .course(courseId != null ? courseRepository.findById(courseId).orElse(null) : null)
                .student(studentId != null ? userRepository.findById(studentId).orElse(null) : null)
                .rating(review.getRating())
                .title(review.getTitle())
                .content(review.getContent())
                .visible(review.isVisible())
                .helpfulCount(review.getHelpfulCount())
                .build();
    }

    private ReviewDTO toDto(Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .course(review.getCourse() != null ? CourseDTO.builder().id(review.getCourse().getId()).build() : null)
                .student(review.getStudent() != null ? UserDTO.builder().id(review.getStudent().getId()).build() : null)
                .rating(review.getRating())
                .title(review.getTitle())
                .content(review.getContent())
                .visible(review.isVisible())
                .helpfulCount(review.getHelpfulCount())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
