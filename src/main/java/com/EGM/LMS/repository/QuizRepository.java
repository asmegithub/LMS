package com.EGM.LMS.repository;

import com.EGM.LMS.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface QuizRepository extends JpaRepository<Quiz, UUID> {
    List<Quiz> findAllByLesson_IdIn(Collection<UUID> lessonIds);
}
