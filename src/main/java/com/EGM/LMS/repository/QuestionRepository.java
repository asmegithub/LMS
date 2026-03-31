package com.EGM.LMS.repository;

import com.EGM.LMS.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
    List<Question> findAllByQuiz_IdIn(Collection<UUID> quizIds);
}
