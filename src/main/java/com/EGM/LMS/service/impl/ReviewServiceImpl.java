package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.CourseDTO;
import com.EGM.LMS.dto.ReviewDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.Review;
import com.EGM.LMS.repository.CourseRepository;
import com.EGM.LMS.repository.EnrollmentRepository;
import com.EGM.LMS.repository.InstructorProfileRepository;
import com.EGM.LMS.repository.ReviewRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final InstructorProfileRepository instructorProfileRepository;

    @Override
    @Transactional
    public ReviewDTO createReview(ReviewDTO review) {
        var student = resolveAuthenticatedUser();
        if (!"STUDENT".equalsIgnoreCase(student.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only students can submit reviews");
        }

        var courseId = review.getCourse() != null ? review.getCourse().getId() : null;
        if (courseId == null) {
            throw new IllegalArgumentException("Course id is required for review");
        }

        var course = courseRepository.findById(courseId).orElseThrow();
        var enrolled = enrollmentRepository.findFirstByStudent_IdAndCourse_Id(student.getId(), courseId);
        if (enrolled.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You must be enrolled to review this course");
        }

        var rating = review.getRating();
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        var existingReview = reviewRepository.findFirstByCourse_IdAndStudent_Id(courseId, student.getId());
        var entity = existingReview.orElseGet(Review::new);
        entity.setCourse(course);
        entity.setStudent(student);
        entity.setRating(rating);
        entity.setTitle(review.getTitle());
        entity.setContent(review.getContent());
        entity.setVisible(true);
        if (existingReview.isEmpty()) {
            entity.setHelpfulCount(0);
        }

        var saved = reviewRepository.save(entity);
        refreshCourseRating(courseId);
        return toDto(saved);
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
        var student = resolveAuthenticatedUser();
        if (!"STUDENT".equalsIgnoreCase(student.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only students can submit reviews");
        }

        var existingReview = reviewRepository.findById(reviewId).orElseThrow();
        if (existingReview.getStudent() == null || !existingReview.getStudent().getId().equals(student.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only edit your own review");
        }

        var course = existingReview.getCourse();
        if (course == null) {
            throw new IllegalArgumentException("Course not found for review");
        }

        var enrolled = enrollmentRepository.findFirstByStudent_IdAndCourse_Id(student.getId(), course.getId());
        if (enrolled.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You must be enrolled to review this course");
        }

        var rating = review.getRating();
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        existingReview.setRating(rating);
        existingReview.setTitle(review.getTitle());
        existingReview.setContent(review.getContent());
        existingReview.setVisible(true);

        var saved = reviewRepository.save(existingReview);
        refreshCourseRating(course.getId());
        refreshInstructorRating(course.getInstructor() != null ? course.getInstructor().getId() : null);
        return toDto(saved);
    }

    @Override
    public void deleteReview(UUID reviewId) {
        var student = resolveAuthenticatedUser();
        if (!"STUDENT".equalsIgnoreCase(student.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only students can delete reviews");
        }

        var review = reviewRepository.findById(reviewId).orElseThrow();
        if (review.getStudent() == null || !review.getStudent().getId().equals(student.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own review");
        }

        var courseId = review.getCourse() != null ? review.getCourse().getId() : null;
        var instructorId = review.getCourse() != null && review.getCourse().getInstructor() != null
            ? review.getCourse().getInstructor().getId()
            : null;

        reviewRepository.deleteById(reviewId);
        if (courseId != null) {
            refreshCourseRating(courseId);
        }
        refreshInstructorRating(instructorId);
    }

    private void refreshCourseRating(UUID courseId) {
        var course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            return;
        }

        var courseReviews = reviewRepository.findAllByCourse_Id(courseId);
        var totalReviews = courseReviews.size();
        var averageRating = courseReviews.stream()
            .mapToInt(Review::getRating)
            .average()
            .orElse(0);

        course.setTotalReviews(totalReviews);
        course.setAverageRating(BigDecimal.valueOf(averageRating).setScale(2, RoundingMode.HALF_UP));
        courseRepository.save(course);
    }

    private void refreshInstructorRating(UUID instructorId) {
        if (instructorId == null) {
            return;
        }

        var instructor = instructorProfileRepository.findById(instructorId).orElse(null);
        if (instructor == null) {
            return;
        }

        var instructorReviews = reviewRepository.findAllByCourse_Instructor_Id(instructorId);
        var averageRating = instructorReviews.stream()
            .mapToInt(Review::getRating)
            .average()
            .orElse(0);

        instructor.setAverageRating(BigDecimal.valueOf(averageRating).setScale(2, RoundingMode.HALF_UP));
        instructorProfileRepository.save(instructor);
    }

    private com.EGM.LMS.model.User resolveAuthenticatedUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required");
        }

        return userRepository.findByEmail(auth.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));
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
        var student = review.getStudent();
        return ReviewDTO.builder()
                .id(review.getId())
                .course(review.getCourse() != null ? CourseDTO.builder().id(review.getCourse().getId()).build() : null)
                .student(student != null ? UserDTO.builder().id(student.getId()).firstName(student.getFirstName()).lastName(student.getLastName()).build() : null)
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
