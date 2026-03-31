package com.EGM.LMS.repository;

import com.EGM.LMS.model.LessonDiscussion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface LessonDiscussionRepository extends JpaRepository<LessonDiscussion, UUID> {
    List<LessonDiscussion> findAllByLesson_IdIn(Collection<UUID> lessonIds);
}
