package com.EGM.LMS.repository;

import com.EGM.LMS.model.LessonDiscussion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LessonDiscussionRepository extends JpaRepository<LessonDiscussion, UUID> {
}
