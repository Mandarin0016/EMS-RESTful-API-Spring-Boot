package org.modis.EmsApplication.service;

import org.modis.EmsApplication.dto.*;
import org.modis.EmsApplication.exception.InvalidOperationException;
import org.modis.EmsApplication.exception.NonexistingEntityException;
import org.modis.EmsApplication.model.*;
import org.modis.EmsApplication.model.enums.Subject;

import java.util.Collection;
import java.util.Set;

public interface SchoolYearService {

    Collection<SchoolYearExposeDTO> getAllSchoolYears();

    SchoolYearExposeDTO getSchoolYearById(Long id) throws NonexistingEntityException;

    SchoolYearExposeDTO create() throws InvalidOperationException;

    SchoolYearDeleteDTO deleteById(Long id) throws NonexistingEntityException;

    Set<Subject> getSchoolYearSubjects(Long id) throws NonexistingEntityException;

    Collection<StudentExposeDTO> getSchoolYearStudents(Long id) throws NonexistingEntityException;

    Collection<TeacherExposeDTO> getSchoolYearTeachers(Long id) throws NonexistingEntityException;

    Collection<ExamExposeDTO> getSchoolYearExams(Long id) throws NonexistingEntityException;

    Collection<HomeworkDTO> getSchoolYearHomework(Long id) throws NonexistingEntityException;

    TimetableDTO getSchoolYearTimetable(Long id) throws NonexistingEntityException;

    TimetableDTO updateSchoolYearTimetable(Long id, UpdateTimetableDTO timetableDTO) throws NonexistingEntityException;

    TimetableDTO createSchoolYearTimetable(Long id, Long timetableId) throws NonexistingEntityException, InvalidOperationException;

    TimetableDTO deleteSchoolYearTimetable(Long id) throws NonexistingEntityException, InvalidOperationException;

    StudentExposeDTO addStudent(Long schoolYearId, Long studentId) throws NonexistingEntityException, InvalidOperationException;

    TeacherExposeDTO addTeacher(Long schoolYearId, Long teacherId) throws NonexistingEntityException, InvalidOperationException;

    Subject addSubject(Long schoolYearId, Subject subject) throws NonexistingEntityException, InvalidOperationException;

    ExamExposeDTO addExam(Long schoolYearId, Long examId) throws NonexistingEntityException, InvalidOperationException;

    HomeworkDTO addHomework(Long schoolYearId, Long homeworkId) throws NonexistingEntityException, InvalidOperationException;

    StudentExposeDTO removeStudent(Long schoolYearId, Long studentId) throws NonexistingEntityException, InvalidOperationException;

    TeacherExposeDTO removeTeacher(Long schoolYearId, Long teacherId) throws NonexistingEntityException, InvalidOperationException;

    Subject removeSubject(Long schoolYearId, Subject subject) throws NonexistingEntityException;

    ExamExposeDTO removeExam(Long schoolYearId, Long examId) throws NonexistingEntityException;

    HomeworkDTO removeHomework(Long schoolYearId, Long homeworkId) throws NonexistingEntityException, InvalidOperationException;

    String schoolYearsCount();
}
