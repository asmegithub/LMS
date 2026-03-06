package com.EGM.LMS.repository;

import com.EGM.LMS.model.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, UUID> {

    List<LessonProgress> findByEnrollment_IdOrderByCreatedAtAsc(UUID enrollmentId);

    Optional<LessonProgress> findByEnrollment_IdAndLesson_Id(UUID enrollmentId, UUID lessonId);
}
