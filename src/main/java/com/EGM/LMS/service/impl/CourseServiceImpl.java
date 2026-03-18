package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.CourseDTO;
import com.EGM.LMS.dto.InstructorProfileDTO;
import com.EGM.LMS.dto.NotificationDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.Course;
import com.EGM.LMS.model.InstructorProfile;
import com.EGM.LMS.repository.CourseCategoryRepository;
import com.EGM.LMS.repository.CourseRepository;
import com.EGM.LMS.repository.InstructorProfileRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.CourseService;
import com.EGM.LMS.service.NotificationService;
import com.EGM.LMS.service.WebPushService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseCategoryRepository courseCategoryRepository;
    private final InstructorProfileRepository instructorProfileRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CourseCategoryServiceImpl courseCategoryServiceImpl;
    private final NotificationService notificationService;

    private final WebPushService webPushService;

    @Override
    public CourseDTO createCourse(CourseDTO coursedto) {
        var savedCourse = courseRepository.save(toEntity(coursedto));
        notifyAdminsCourseCreated(savedCourse);
        return toDto(savedCourse);
    }

    @Override
    public List<CourseDTO> getAllCourses() {
        var courses = courseRepository.findAll();
        var coursesDtos = new ArrayList<CourseDTO>();
        for (Course c : courses) {
            coursesDtos.add(toDto(c));
        }
        return coursesDtos;
    }

    @Override
    public List<CourseDTO> getCoursesByStatus(String status) {
        var normalizedStatus = status == null ? null : status.trim().toUpperCase();
        if (normalizedStatus == null || normalizedStatus.isBlank()) {
            return getAllCourses();
        }

        var courses = courseRepository.findByStatus(normalizedStatus);
        var coursesDtos = new ArrayList<CourseDTO>();
        for (Course course : courses) {
            coursesDtos.add(toDto(course));
        }
        return coursesDtos;
    }

    @Override
    public CourseDTO getCourse(UUID courseId) {
        var course = courseRepository.findById(courseId).orElseThrow();
        return toDto(course);

    }

    @Override
    public CourseDTO updateCourse(UUID courseId, CourseDTO coursedto) {
        var existingCourse = courseRepository.findById(courseId).orElseThrow();
        existingCourse.setDescription(coursedto.getDescription());
        existingCourse.setDescriptionAm(coursedto.getDescriptionAm());
        existingCourse.setDescriptionGz(coursedto.getDescriptionGz());
        existingCourse.setDescriptionOm(coursedto.getDescriptionOm());
        existingCourse.setTitle(coursedto.getTitle());
        existingCourse.setTitleAm(coursedto.getTitleAm());
        existingCourse.setTitleGz(coursedto.getTitleGz());
        existingCourse.setTitleOm(coursedto.getTitleOm());
        existingCourse.setThumbnail(coursedto.getThumbnail());
        existingCourse.setPreviewVideo(coursedto.getPreviewVideo());
        existingCourse.setPrice(coursedto.getPrice());
        existingCourse.setDiscountPrice(coursedto.getDiscountPrice());
        existingCourse.setCurrency(coursedto.getCurrency());
        existingCourse.setLevel(coursedto.getLevel());
        existingCourse.setStatus(coursedto.getStatus());
        existingCourse.setTotalLessons(coursedto.getTotalLessons() != null ? coursedto.getTotalLessons() : 0);
        existingCourse.setTotalDuration(coursedto.getTotalDuration() != null ? coursedto.getTotalDuration() : 0);
        existingCourse.setSlug(coursedto.getSlug());
        existingCourse.setCategory(courseCategoryRepository.findById(coursedto.getCategoryId()).orElse(null));

        var resolvedInstructor = resolveInstructor(coursedto);
        if (resolvedInstructor != null) {
            existingCourse.setInstructor(resolvedInstructor);
        }

        return toDto(courseRepository.save(existingCourse));
    }

    @Override
    public void deleteCourse(UUID courseId) {
        courseRepository.deleteById(courseId);
    }

    @Override
    public CourseDTO setFeatured(UUID courseId, boolean isFeatured) {
        requireAdmin();
        var existingCourse = courseRepository.findById(courseId).orElseThrow(() -> new IllegalArgumentException("Course not found"));
        existingCourse.setFeatured(isFeatured);
        return toDto(courseRepository.save(existingCourse));
    }

    // mapper methods
    Course toEntity(CourseDTO coursedto) {
        return Course.builder()
                .category(courseCategoryRepository.findById(coursedto.getCategoryId()).orElse(null))
                .instructor(resolveInstructor(coursedto))
                .title(coursedto.getTitle())
                .titleAm(coursedto.getTitleAm())
                .titleGz(coursedto.getTitleGz())
                .titleOm(coursedto.getTitleOm())

                .slug(coursedto.getSlug())
                .totalDuration(coursedto.getTotalDuration() != null ? coursedto.getTotalDuration() : 0)
                .totalLessons(coursedto.getTotalLessons() != null ? coursedto.getTotalLessons() : 0)

                .thumbnail(coursedto.getThumbnail())
                .previewVideo(coursedto.getPreviewVideo())
                .level(coursedto.getLevel())
                .status(coursedto.getStatus())

                .description(coursedto.getDescription())
                .descriptionAm(coursedto.getDescriptionAm())
                .descriptionGz(coursedto.getDescriptionGz())
                .descriptionOm(coursedto.getDescriptionOm())
                .price(coursedto.getPrice())
                .discountPrice(coursedto.getDiscountPrice())
                .currency(coursedto.getCurrency())
                .isFeatured(Boolean.TRUE.equals(coursedto.getIsFeatured()))
                .isPopular(Boolean.TRUE.equals(coursedto.getIsPopular()))

                .build();
    }

    CourseDTO toDto(Course course) {
        // instructor
        var instructor = course.getInstructor();
        return CourseDTO.builder()
                .id(course.getId())
                .categoryId(course.getCategory() != null ? course.getCategory().getId() : null)
                .category(
                        course.getCategory() != null
                                ? courseCategoryServiceImpl.toDto(
                                        courseCategoryRepository.findById(course.getCategory().getId()).orElseThrow())
                                : null)
                .instructor(InstructorProfileDTO.builder()
                        .id(instructor != null ? instructor.getId() : null)
                        .user(instructor != null ? UserDTO.builder()
                                .id(instructor.getUser() != null ? instructor.getUser().getId() : null)
                                .email(instructor.getUser() != null ? instructor.getUser().getEmail() : null)
                                .firstName(instructor.getUser() != null ? instructor.getUser().getFirstName() : null)
                                .lastName(instructor.getUser() != null ? instructor.getUser().getLastName() : null)

                                .build() : null)
                        .headline(instructor != null ? instructor.getHeadline() : null)
                        .averageRating(instructor != null ? instructor.getAverageRating() : BigDecimal.ZERO)
                        .isVerified(instructor != null && instructor.isVerified())
                        .averageRating(instructor != null ? instructor.getAverageRating() : BigDecimal.ZERO)
                        .totalCourses(instructor != null ? instructor.getTotalCourses() : 0)
                        .totalStudents(instructor != null ? instructor.getTotalStudents() : 0)
                        .build())
                .title(course.getTitle())
                .titleAm(course.getTitleAm())
                .titleGz(course.getTitleGz())
                .titleOm(course.getTitleOm())

                .slug(course.getSlug())
                .totalDuration(course.getTotalDuration())
                .totalLessons(course.getTotalLessons())

                .thumbnail(course.getThumbnail())
                .previewVideo(course.getPreviewVideo())
                .level(course.getLevel())
                .status(course.getStatus())

                .description(course.getDescription())
                .descriptionAm(course.getDescriptionAm())
                .descriptionGz(course.getDescriptionGz())
                .descriptionOm(course.getDescriptionOm())

                .price(course.getPrice())
                .discountPrice(course.getDiscountPrice())
                .currency(course.getCurrency())
                .isFeatured(course.isFeatured())
                .isPopular(course.isPopular())
                .averageRating(course.getAverageRating())
                .enrollmentCount(course.getEnrollmentCount())
                .totalReviews(course.getTotalReviews())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }

    private void requireAdmin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        var user = userRepository.findByEmail(auth.getName()).orElse(null);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }
        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin only");
        }
    }

    private InstructorProfile resolveInstructor(CourseDTO coursedto) {
        if (coursedto.getInstructorId() != null) {
            var instructorByProfileId = instructorProfileRepository.findById(coursedto.getInstructorId());
            if (instructorByProfileId.isPresent()) {
                return instructorByProfileId.get();
            }

            var instructorByUserId = instructorProfileRepository.findFirstByUser_Id(coursedto.getInstructorId());
            if (instructorByUserId.isPresent()) {
                return instructorByUserId.get();
            }
        }

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            return null;
        }

        var user = userRepository.findByEmail(auth.getName()).orElse(null);
        if (user == null) {
            return null;
        }

        return instructorProfileRepository.findFirstByUser_Id(user.getId())
                .orElseGet(() -> instructorProfileRepository.save(InstructorProfile.builder()
                        .user(user)
                        .headline(user.getFirstName() != null ? user.getFirstName() : "")
                        .biography("")
                        .totalStudents(0)
                        .totalCourses(0)
                        .totalRevenue(BigDecimal.ZERO)
                        .averageRating(BigDecimal.ZERO)
                        .isVerified(false)
                        .build()));
    }

    private void notifyAdminsCourseCreated(Course course) {
        var admins = userRepository.findByRoleIgnoreCase("ADMIN");
        if (admins.isEmpty()) {
            admins = userRepository.findByRoleIgnoreCase("ROLE_ADMIN");
        }
        if (admins.isEmpty()) {
            return;
        }

        var instructorName = "an instructor";
        if (course.getInstructor() != null && course.getInstructor().getUser() != null) {
            var firstName = Optional.ofNullable(course.getInstructor().getUser().getFirstName()).orElse("");
            var lastName = Optional.ofNullable(course.getInstructor().getUser().getLastName()).orElse("");
            var fullName = (firstName + " " + lastName).trim();
            if (!fullName.isBlank()) {
                instructorName = fullName;
            }
        }

        var title = "New course submitted";
        var message = instructorName + " submitted \"" + course.getTitle() + "\" for review.";
        var actionUrl = "/admin/approvals";

        for (var admin : admins) {
            var notification = NotificationDTO.builder()
                    .user(UserDTO.builder().id(admin.getId()).build())
                    .type("SYSTEM")
                    .title(title)
                    .message(message)
                    .isRead(false)
                    .relatedId(course.getId() != null ? course.getId().toString() : null)
                    .relatedType("COURSE")
                    .actionUrl(actionUrl)
                    .build();
            notificationService.createNotification(notification);
        }
        webPushService.sendPushToUsers(admins, title, message, actionUrl);
    }

}
