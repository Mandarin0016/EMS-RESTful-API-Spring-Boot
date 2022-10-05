package org.modis.EmsApplication.dao;

import org.modis.EmsApplication.model.Competition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {
}
