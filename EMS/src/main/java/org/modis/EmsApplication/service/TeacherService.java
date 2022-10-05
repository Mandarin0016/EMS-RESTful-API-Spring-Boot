package org.modis.EmsApplication.service;

import org.modis.EmsApplication.dto.TeacherExposeDTO;
import org.modis.EmsApplication.dto.TimetableDTO;
import org.modis.EmsApplication.dto.UserExposeDTO;
import org.modis.EmsApplication.exception.InvalidOperationException;
import org.modis.EmsApplication.exception.NonexistingEntityException;
import org.modis.EmsApplication.model.SchoolYear;
import org.modis.EmsApplication.model.Timetable;
import org.modis.EmsApplication.model.User;

import java.util.List;

public interface TeacherService {

    List<TeacherExposeDTO> getAllTeachers();

    TeacherExposeDTO getTeacherById(Long id);

    TimetableDTO getTimetable(Long teacherId) throws NonexistingEntityException, InvalidOperationException;

    TimetableDTO setTimetable(Long teacherId, Long timetableId) throws NonexistingEntityException, InvalidOperationException;

    TimetableDTO deleteTimetable(Long teacherId) throws NonexistingEntityException, InvalidOperationException;

}
