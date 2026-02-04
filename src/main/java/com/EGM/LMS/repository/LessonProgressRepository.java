package com.EGM.LMS.repository;

import com.EGM.LMS.model.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, UUID> {
}
