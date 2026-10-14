package com.aptitudearena.backend.repository;

import com.aptitudearena.backend.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
	List<Question> findBySubject_Id(UUID subjectId);

	@Query("select question from Question question join fetch question.subject where question.subject.id = :subjectId")
	List<Question> findWithSubjectBySubjectId(@Param("subjectId") UUID subjectId);

	@Query("""
			select question from Question question
			join fetch question.subject
			where question.subject.id in (
				select subject.id from Exam exam join exam.subjects subject where exam.id = :examId
			)
			""")
	List<Question> findWithSubjectByExamId(@Param("examId") UUID examId);

	@Query("select question from Question question join fetch question.subject where question.id = :id")
	Optional<Question> findWithSubjectById(@Param("id") UUID id);
}
