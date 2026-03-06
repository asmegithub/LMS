package com.EGM.LMS.repository;

import com.EGM.LMS.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {

    List<Lesson> findBySection_Course_IdOrderBySection_OrderIndexAscOrderIndexAsc(UUID courseId);
}
