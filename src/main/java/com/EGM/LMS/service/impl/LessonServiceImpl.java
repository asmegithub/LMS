package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.CourseSectionDTO;
import com.EGM.LMS.dto.LessonDTO;
import com.EGM.LMS.model.Lesson;
import com.EGM.LMS.model.User;
import com.EGM.LMS.repository.CourseSectionRepository;
import com.EGM.LMS.repository.EnrollmentRepository;
import com.EGM.LMS.repository.LessonRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    @Override
    public LessonDTO createLesson(LessonDTO lesson) {
        return toDto(lessonRepository.save(toEntity(lesson)));
    }

    @Override
    public List<LessonDTO> getAllLessons() {
        var user = resolveAuthenticatedUserOrNull();
        var lessons = lessonRepository.findAll();
        var lessonDtos = new java.util.ArrayList<LessonDTO>();
        for (Lesson lesson : lessons) {
            var dto = toDto(lesson);
            lessonDtos.add(canAccessFullLessonContent(lesson, user) ? dto : toLockedDto(dto));
        }
        return lessonDtos;
    }

    @Override
    public List<LessonDTO> getLessonsByCourseId(UUID courseId) {
        var user = resolveAuthenticatedUserOrNull();
        var canAccessPaidLessons = canAccessPaidLessonsInCourse(courseId, user);
        var lessons = lessonRepository.findBySection_Course_IdOrderBySection_OrderIndexAscOrderIndexAsc(courseId);
        var lessonDtos = new java.util.ArrayList<LessonDTO>();
        for (Lesson lesson : lessons) {
            var dto = toDto(lesson);
            if (Boolean.TRUE.equals(lesson.getIsFree()) || canAccessPaidLessons) {
                lessonDtos.add(dto);
            } else {
                lessonDtos.add(toLockedDto(dto));
            }
        }
        return lessonDtos;
    }

    @Override
    public LessonDTO getLesson(UUID lessonId) {
        var user = resolveAuthenticatedUserOrNull();
        var lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found"));
        var dto = toDto(lesson);
        return canAccessFullLessonContent(lesson, user) ? dto : toLockedDto(dto);
    }

    @Override
    public LessonDTO updateLesson(UUID lessonId, LessonDTO lesson) {
        lessonRepository.findById(lessonId).orElseThrow();
        var entity = toEntity(lesson);
        entity.setId(lessonId);
        return toDto(lessonRepository.save(entity));
    }

    @Override
    public void deleteLesson(UUID lessonId) {
        lessonRepository.deleteById(lessonId);
    }

    private Lesson toEntity(LessonDTO lesson) {
        var sectionId = lesson.getSection() != null ? lesson.getSection().getId() : null;
        return Lesson.builder()
                .section(sectionId != null ? courseSectionRepository.findById(sectionId).orElse(null) : null)
                .title(lesson.getTitle())
                .titleAm(lesson.getTitleAm())
                .titleOm(lesson.getTitleOm())
                .titleGz(lesson.getTitleGz())
                .type(lesson.getType())
                .videoUrl(lesson.getVideoUrl())
                .videoUrl240p(lesson.getVideoUrl240p())
                .videoUrl360p(lesson.getVideoUrl360p())
                .videoUrl720p(lesson.getVideoUrl720p())
                .encryptedVideoUrl(lesson.getEncryptedVideoUrl())
                .duration(lesson.getDuration())
                .documentUrl(lesson.getDocumentUrl())
                .documentType(lesson.getDocumentType())
                .content(lesson.getContent())
                .orderIndex(lesson.getOrderIndex())
                .isFree(lesson.getIsFree())
                .isDownloadable(lesson.getIsDownloadable())
                .isPublished(lesson.getIsPublished())
                .build();
    }

    private LessonDTO toDto(Lesson lesson) {
        return LessonDTO.builder()
                .id(lesson.getId())
                .section(lesson.getSection() != null ? CourseSectionDTO.builder().id(lesson.getSection().getId()).build() : null)
                .title(lesson.getTitle())
                .titleAm(lesson.getTitleAm())
                .titleOm(lesson.getTitleOm())
                .titleGz(lesson.getTitleGz())
                .type(lesson.getType())
                .videoUrl(lesson.getVideoUrl())
                .videoUrl240p(lesson.getVideoUrl240p())
                .videoUrl360p(lesson.getVideoUrl360p())
                .videoUrl720p(lesson.getVideoUrl720p())
                .encryptedVideoUrl(lesson.getEncryptedVideoUrl())
                .duration(lesson.getDuration())
                .documentUrl(lesson.getDocumentUrl())
                .documentType(lesson.getDocumentType())
                .content(lesson.getContent())
                .orderIndex(lesson.getOrderIndex())
                .isFree(lesson.getIsFree())
                .isDownloadable(lesson.getIsDownloadable())
                .isPublished(lesson.getIsPublished())
                .createdAt(lesson.getCreatedAt())
                .updatedAt(lesson.getUpdatedAt())
                .build();
    }

    private LessonDTO toLockedDto(LessonDTO lesson) {
        lesson.setVideoUrl(null);
        lesson.setVideoUrl240p(null);
        lesson.setVideoUrl360p(null);
        lesson.setVideoUrl720p(null);
        lesson.setEncryptedVideoUrl(null);
        lesson.setDocumentUrl(null);
        lesson.setContent(null);
        lesson.setIsDownloadable(false);
        return lesson;
    }

    private boolean canAccessPaidLessonsInCourse(UUID courseId, User user) {
        if (user == null) return false;
        if (isPrivileged(user)) return true;
        if (isInstructorOfCourse(courseId, user)) return true;
        return enrollmentRepository.findFirstByStudent_IdAndCourse_Id(user.getId(), courseId).isPresent();
    }

    private boolean canAccessFullLessonContent(Lesson lesson, User user) {
        if (Boolean.TRUE.equals(lesson.getIsFree())) return true;
        if (user == null) return false;
        if (isPrivileged(user)) return true;
        var courseId = lesson.getSection() != null && lesson.getSection().getCourse() != null
                ? lesson.getSection().getCourse().getId()
                : null;
        var instructorUserId = lesson.getSection() != null
                && lesson.getSection().getCourse() != null
                && lesson.getSection().getCourse().getInstructor() != null
                && lesson.getSection().getCourse().getInstructor().getUser() != null
                ? lesson.getSection().getCourse().getInstructor().getUser().getId()
                : null;
        if (instructorUserId != null && instructorUserId.equals(user.getId())) return true;
        if (courseId == null) return false;
        return enrollmentRepository.findFirstByStudent_IdAndCourse_Id(user.getId(), courseId).isPresent();
    }

    private boolean isInstructorOfCourse(UUID courseId, User user) {
        if (courseId == null || user == null) return false;
        return courseSectionRepository.findByCourse_IdOrderByOrderIndexAsc(courseId).stream()
                .findFirst()
                .map(s -> s.getCourse())
                .map(c -> c.getInstructor())
                .map(i -> i.getUser())
                .map(u -> user.getId().equals(u.getId()))
                .orElse(false);
    }

    private boolean isPrivileged(User user) {
        if (user == null || user.getRole() == null) return false;
        return "ADMIN".equalsIgnoreCase(user.getRole());
    }

    private User resolveAuthenticatedUserOrNull() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank() || "anonymousUser".equals(auth.getName())) {
            return null;
        }
        return userRepository.findByEmail(auth.getName()).orElse(null);
    }
}
