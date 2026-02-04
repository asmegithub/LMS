package com.EGM.LMS.repository;

import com.EGM.LMS.model.LessonResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LessonResourceRepository extends JpaRepository<LessonResource, UUID> {
}
