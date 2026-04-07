package com.EGM.LMS.repository;

import com.EGM.LMS.model.LessonResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface LessonResourceRepository extends JpaRepository<LessonResource, UUID> {
    List<LessonResource> findAllByLesson_Id(UUID lessonId);

    List<LessonResource> findAllByLesson_IdIn(Collection<UUID> lessonIds);
}
