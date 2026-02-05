package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.CourseDTO;
import com.EGM.LMS.dto.CourseRatingDTO;
import com.EGM.LMS.model.CourseRating;
import com.EGM.LMS.repository.CourseRatingRepository;
import com.EGM.LMS.repository.CourseRepository;
import com.EGM.LMS.service.CourseRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseRatingServiceImpl implements CourseRatingService {
    private final CourseRatingRepository courseRatingRepository;
    private final CourseRepository courseRepository;

    @Override
    public CourseRatingDTO createCourseRating(CourseRatingDTO courseRating) {
        return toDto(courseRatingRepository.save(toEntity(courseRating)));
    }

    @Override
    public List<CourseRatingDTO> getAllCourseRatings() {
        var ratings = courseRatingRepository.findAll();
        var ratingDtos = new java.util.ArrayList<CourseRatingDTO>();
        for (CourseRating rating : ratings) {
            ratingDtos.add(toDto(rating));
        }
        return ratingDtos;
    }

    @Override
    public CourseRatingDTO getCourseRating(UUID courseRatingId) {
        return toDto(courseRatingRepository.findById(courseRatingId).orElseThrow());
    }

    @Override
    public CourseRatingDTO updateCourseRating(UUID courseRatingId, CourseRatingDTO courseRating) {
        courseRatingRepository.findById(courseRatingId).orElseThrow();
        var entity = toEntity(courseRating);
        entity.setId(courseRatingId);
        return toDto(courseRatingRepository.save(entity));
    }

    @Override
    public void deleteCourseRating(UUID courseRatingId) {
        courseRatingRepository.deleteById(courseRatingId);
    }

    private CourseRating toEntity(CourseRatingDTO courseRating) {
        var courseId = courseRating.getCourse() != null ? courseRating.getCourse().getId() : null;
        return CourseRating.builder()
                .course(courseId != null ? courseRepository.findById(courseId).orElse(null) : null)
                .oneStar(courseRating.getOneStar())
                .twoStar(courseRating.getTwoStar())
                .threeStar(courseRating.getThreeStar())
                .fourStar(courseRating.getFourStar())
                .fiveStar(courseRating.getFiveStar())
                .totalCount(courseRating.getTotalCount())
                .average(courseRating.getAverage())
                .build();
    }

    private CourseRatingDTO toDto(CourseRating courseRating) {
        return CourseRatingDTO.builder()
                .id(courseRating.getId())
                .course(courseRating.getCourse() != null ? CourseDTO.builder().id(courseRating.getCourse().getId()).build() : null)
                .oneStar(courseRating.getOneStar())
                .twoStar(courseRating.getTwoStar())
                .threeStar(courseRating.getThreeStar())
                .fourStar(courseRating.getFourStar())
                .fiveStar(courseRating.getFiveStar())
                .totalCount(courseRating.getTotalCount())
                .average(courseRating.getAverage())
                .createdAt(courseRating.getCreatedAt())
                .updatedAt(courseRating.getUpdatedAt())
                .build();
    }
}
