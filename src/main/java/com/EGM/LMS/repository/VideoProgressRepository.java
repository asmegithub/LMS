package com.EGM.LMS.repository;

import com.EGM.LMS.model.VideoProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VideoProgressRepository extends JpaRepository<VideoProgress, UUID> {
    java.util.Optional<VideoProgress> findByEnrollment_IdAndLesson_Id(UUID enrollmentId, UUID lessonId);
    List<VideoProgress> findByEnrollment_Id(UUID enrollmentId);
}
