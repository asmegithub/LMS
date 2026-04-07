package com.EGM.LMS.repository;

import com.EGM.LMS.model.QuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, UUID> {
	List<QuizAnswer> findAllByAttempt_Id(UUID attemptId);
	List<QuizAnswer> findAllByAttempt_Student_Id(UUID studentId);
	List<QuizAnswer> findAllByAttempt_IdIn(List<UUID> attemptIds);
}
