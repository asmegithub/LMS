package com.EGM.LMS.repository;

import com.EGM.LMS.model.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface QuestionOptionRepository extends JpaRepository<QuestionOption, UUID> {
    List<QuestionOption> findAllByQuestion_IdIn(Collection<UUID> questionIds);
}
