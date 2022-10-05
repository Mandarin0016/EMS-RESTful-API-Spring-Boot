package org.modis.EmsApplication.dao;

import org.modis.EmsApplication.model.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {
    Optional<Timetable> findByTitle(String title);

    Optional<Timetable> findByContentUrl(String contentUrl);
}
