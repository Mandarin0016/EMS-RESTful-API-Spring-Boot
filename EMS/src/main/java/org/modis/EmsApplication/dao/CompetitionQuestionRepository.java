package org.modis.EmsApplication.dao;

import org.modis.EmsApplication.model.CompetitionQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetitionQuestionRepository extends JpaRepository<CompetitionQuestion, Long> {
}
