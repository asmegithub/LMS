package com.EGM.LMS.repository;

import com.EGM.LMS.model.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, UUID> {
	List<QuizAttempt> findAllByStudent_Id(UUID studentId);
	List<QuizAttempt> findAllByStudent_IdAndQuiz_Id(UUID studentId, UUID quizId);
	List<QuizAttempt> findAllByQuiz_Id(UUID quizId);
}
