package org.modis.EmsApplication.service;

import org.modis.EmsApplication.dto.TimetableDTO;
import org.modis.EmsApplication.dto.UpdateTimetableDTO;
import org.modis.EmsApplication.exception.InvalidEntityDataException;
import org.modis.EmsApplication.exception.NonexistingEntityException;
import org.modis.EmsApplication.model.Timetable;

import java.util.Collection;

public interface TimetableService {
    Collection<TimetableDTO> getAllTimetables();

    TimetableDTO getTimetableById(Long id) throws NonexistingEntityException;

    Timetable getTimetableByIdModel(Long id) throws NonexistingEntityException;

    TimetableDTO create(TimetableDTO timetableDTO) throws InvalidEntityDataException;

    TimetableDTO update(Long id, UpdateTimetableDTO timetableDTO) throws NonexistingEntityException;

    Timetable updateModel(Long id, UpdateTimetableDTO timetableDTO) throws NonexistingEntityException;

    TimetableDTO deleteById(Long id) throws NonexistingEntityException;

    String count();
}
