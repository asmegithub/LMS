package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.CourseDTO;
import com.EGM.LMS.dto.InstructorProfileDTO;
import com.EGM.LMS.dto.NotificationDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.Course;
import com.EGM.LMS.model.InstructorProfile;
import com.EGM.LMS.repository.CourseCategoryRepository;
import com.EGM.LMS.repository.CourseOutcomeRepository;
import com.EGM.LMS.repository.CourseRepository;
import com.EGM.LMS.repository.CourseRequirementRepository;
import com.EGM.LMS.repository.CourseSectionRepository;
import com.EGM.LMS.repository.DiscussionReplyRepository;
import com.EGM.LMS.repository.InstructorProfileRepository;
import com.EGM.LMS.repository.LessonDiscussionRepository;
import com.EGM.LMS.repository.LessonRepository;
import com.EGM.LMS.repository.LessonResourceRepository;
import com.EGM.LMS.repository.QuestionOptionRepository;
import com.EGM.LMS.repository.QuestionRepository;
import com.EGM.LMS.repository.QuizRepository;
import com.EGM.LMS.repository.BookmarkRepository;
import com.EGM.LMS.repository.ReviewRepository;
import com.EGM.LMS.repository.EnrollmentRepository;
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
    private final CourseSectionRepository courseSectionRepository;
    private final LessonRepository lessonRepository;
    private final LessonResourceRepository lessonResourceRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final LessonDiscussionRepository lessonDiscussionRepository;
    private final DiscussionReplyRepository discussionReplyRepository;
    private final BookmarkRepository bookmarkRepository;
    private final CourseOutcomeRepository courseOutcomeRepository;
    private final CourseRequirementRepository courseRequirementRepository;
    private final ReviewRepository reviewRepository;
    private final EnrollmentRepository enrollmentRepository;
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
        var actor = requireCurrentUser();
        var isAdmin = "ADMIN".equalsIgnoreCase(actor.getRole());
        var isOwnerInstructor = existingCourse.getInstructor() != null
                && existingCourse.getInstructor().getUser() != null
                && Objects.equals(existingCourse.getInstructor().getUser().getId(), actor.getId());
        if (!isAdmin && !isOwnerInstructor) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only edit your own course");
        }

        var existingStatus = Optional.ofNullable(existingCourse.getStatus()).orElse("DRAFT").trim().toUpperCase();
        var isApprovedCourse = existingStatus.equals("APPROVED") || existingStatus.equals("PUBLISHED");

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
        existingCourse.setTotalLessons(coursedto.getTotalLessons() != null ? coursedto.getTotalLessons() : 0);
        existingCourse.setTotalDuration(coursedto.getTotalDuration() != null ? coursedto.getTotalDuration() : 0);
        existingCourse.setSlug(coursedto.getSlug());
        existingCourse.setCategory(courseCategoryRepository.findById(coursedto.getCategoryId()).orElse(null));

        var resolvedInstructor = resolveInstructor(coursedto);
        if (resolvedInstructor != null) {
            existingCourse.setInstructor(resolvedInstructor);
        }

        if (isAdmin) {
            existingCourse.setStatus(coursedto.getStatus());
            if (coursedto.getIsPublished() != null) {
                existingCourse.setPublished(coursedto.getIsPublished());
            }
        } else if (isApprovedCourse) {
            // Maker-checker: instructor changes to approved course require admin re-approval.
            existingCourse.setStatus("PENDING");
            existingCourse.setPublished(false);
            notifyAdminsCourseResubmitted(existingCourse, actor.getFirstName(), actor.getLastName());
        } else if (coursedto.getStatus() != null && !coursedto.getStatus().isBlank()) {
            // For non-approved courses, instructors can still keep their local status workflow.
            existingCourse.setStatus(coursedto.getStatus());
        }

        return toDto(courseRepository.save(existingCourse));
    }

    @Override
    public void deleteCourse(UUID courseId) {
        var course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        var user = userRepository.findByEmail(auth.getName()).orElse(null);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }

        boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRole());
        boolean isOwnerInstructor = course.getInstructor() != null
                && course.getInstructor().getUser() != null
                && Objects.equals(course.getInstructor().getUser().getId(), user.getId());

        if (!isAdmin && !isOwnerInstructor) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own course");
        }

        String status = course.getStatus() != null ? course.getStatus().trim().toUpperCase() : "";
        if (!isAdmin && !(status.equals("PENDING") || status.equals("DRAFT") || status.equals("REJECTED"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only pending/draft/rejected courses can be deleted");
        }

        // Guard against deleting active courses with enrollments for non-admins.
        var enrollments = enrollmentRepository.findAllByCourse_Id(courseId);
        if (!isAdmin && !enrollments.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course has enrollments and cannot be deleted");
        }

        // Remove dependent rows first to avoid FK violations.
        reviewRepository.deleteAll(reviewRepository.findAllByCourse_Id(courseId));
        courseOutcomeRepository.deleteAll(courseOutcomeRepository.findAllByCourse_Id(courseId));
        courseRequirementRepository.deleteAll(courseRequirementRepository.findAllByCourse_Id(courseId));

        var sections = courseSectionRepository.findByCourse_IdOrderByOrderIndexAsc(courseId);
        var sectionIds = sections.stream().map(s -> s.getId()).filter(Objects::nonNull).toList();
        var lessons = lessonRepository.findBySection_Course_IdOrderBySection_OrderIndexAscOrderIndexAsc(courseId);
        var lessonIds = lessons.stream().map(l -> l.getId()).filter(Objects::nonNull).toList();

        if (!lessonIds.isEmpty()) {
            var discussions = lessonDiscussionRepository.findAllByLesson_IdIn(lessonIds);
            var discussionIds = discussions.stream().map(d -> d.getId()).filter(Objects::nonNull).toList();
            if (!discussionIds.isEmpty()) {
                discussionReplyRepository.deleteAll(discussionReplyRepository.findAllByDiscussion_IdIn(discussionIds));
            }
            lessonDiscussionRepository.deleteAll(discussions);

            bookmarkRepository.deleteAll(bookmarkRepository.findAllByLesson_IdIn(lessonIds));
            lessonResourceRepository.deleteAll(lessonResourceRepository.findAllByLesson_IdIn(lessonIds));

            var quizzes = quizRepository.findAllByLesson_IdIn(lessonIds);
            var quizIds = quizzes.stream().map(q -> q.getId()).filter(Objects::nonNull).toList();
            if (!quizIds.isEmpty()) {
                var questions = questionRepository.findAllByQuiz_IdIn(quizIds);
                var questionIds = questions.stream().map(q -> q.getId()).filter(Objects::nonNull).toList();
                if (!questionIds.isEmpty()) {
                    questionOptionRepository.deleteAll(questionOptionRepository.findAllByQuestion_IdIn(questionIds));
                }
                questionRepository.deleteAll(questions);
            }
            quizRepository.deleteAll(quizzes);

            lessonRepository.deleteAll(lessons);
        }

        if (!sectionIds.isEmpty()) {
            courseSectionRepository.deleteAll(sections);
        }

        courseRepository.delete(course);
    }

    @Override
    public CourseDTO setFeatured(UUID courseId, boolean isFeatured) {
        requireAdmin();
        var existingCourse = courseRepository.findById(courseId).orElseThrow(() -> new IllegalArgumentException("Course not found"));
        if (isFeatured && !existingCourse.isPublished()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hidden courses cannot be featured");
        }
        existingCourse.setFeatured(isFeatured);
        return toDto(courseRepository.save(existingCourse));
    }

    @Override
    public CourseDTO setPublished(UUID courseId, boolean isPublished) {
        var existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        var actor = requireCurrentUser();
        var isAdmin = "ADMIN".equalsIgnoreCase(actor.getRole());
        var isOwnerInstructor = existingCourse.getInstructor() != null
                && existingCourse.getInstructor().getUser() != null
                && Objects.equals(existingCourse.getInstructor().getUser().getId(), actor.getId());

        if (!isAdmin && !isOwnerInstructor) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only manage visibility of your own course");
        }

        var status = Optional.ofNullable(existingCourse.getStatus()).orElse("").trim().toUpperCase();
        if (!isAdmin && !(status.equals("APPROVED") || status.equals("PUBLISHED"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only approved courses can be hidden/unhidden");
        }

        existingCourse.setPublished(isPublished);
        if (!isPublished && existingCourse.isFeatured()) {
            // Keep featured list consistent with visibility.
            existingCourse.setFeatured(false);
        }
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
                .isPublished(coursedto.getIsPublished() == null || coursedto.getIsPublished())

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
                .isPublished(course.isPublished())
                .averageRating(course.getAverageRating())
                .enrollmentCount(course.getEnrollmentCount())
                .totalReviews(course.getTotalReviews())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }

    private void requireAdmin() {
        var user = requireCurrentUser();
        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin only");
        }
    }

    private com.EGM.LMS.model.User requireCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        var user = userRepository.findByEmail(auth.getName()).orElse(null);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }
        return user;
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

    private void notifyAdminsCourseResubmitted(Course course, String actorFirstName, String actorLastName) {
        var admins = userRepository.findByRoleIgnoreCase("ADMIN");
        if (admins.isEmpty()) {
            admins = userRepository.findByRoleIgnoreCase("ROLE_ADMIN");
        }
        if (admins.isEmpty()) {
            return;
        }

        var instructorName = (Optional.ofNullable(actorFirstName).orElse("") + " " + Optional.ofNullable(actorLastName).orElse("")).trim();
        if (instructorName.isBlank()) {
            instructorName = "an instructor";
        }

        var title = "Course update submitted";
        var message = instructorName + " updated \"" + course.getTitle() + "\" and submitted it for re-approval.";
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
