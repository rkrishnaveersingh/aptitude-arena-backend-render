package com.aptitudearena.backend.repository;

import com.aptitudearena.backend.entity.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface QuestionOptionRepository extends JpaRepository<QuestionOption, UUID> {

	@Query("""
			select option.id as id,
				   option.question.id as questionId,
				   option.optionLabel as optionLabel,
				   option.optionText as optionText,
				   option.isCorrect as correct
			from QuestionOption option
			where option.question.id in :questionIds
			""")
	List<OptionView> findOptionsByQuestionIds(@Param("questionIds") Collection<UUID> questionIds);

	interface OptionView {
		UUID getId();

		UUID getQuestionId();

		String getOptionLabel();

		String getOptionText();

		boolean getCorrect();
	}
}
