package com.aptitudearena.backend.repository;

import com.aptitudearena.backend.entity.ExamQuestion;
import com.aptitudearena.backend.entity.ExamQuestion.ExamQuestionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, ExamQuestionId> {
	List<ExamQuestion> findByExam_IdOrderByQuestionOrderAsc(UUID examId);
}
