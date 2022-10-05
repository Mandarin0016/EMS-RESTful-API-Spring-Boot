package org.modis.EmsApplication.service;

import org.modis.EmsApplication.dto.*;
import org.modis.EmsApplication.exception.InvalidEntityDataException;
import org.modis.EmsApplication.exception.InvalidOperationException;
import org.modis.EmsApplication.exception.NonexistingEntityException;
import org.modis.EmsApplication.model.*;
import org.modis.EmsApplication.model.enums.Subject;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface StudentService {

    Collection<StudentExposeDTO> getAllStudents();

    StudentExposeDTO getStudentById(Long id);

    User getStudentByIdModel(Long id);

    Map<Subject, List<BigDecimal>> getAllStudentGrades(Long id) throws NonexistingEntityException, InvalidOperationException;

    Map<Subject, List<BigDecimal>> addStudentGrade(Long id, String subjectName, String grade) throws NonexistingEntityException, InvalidOperationException;

    Map<Subject, List<BigDecimal>> deleteStudentGrade(Long id, String subjectName, String grade) throws NonexistingEntityException, InvalidOperationException;

    List<HomeworkDTO> getAllStudentHomework(Long id) throws InvalidOperationException;

    HomeworkDTO addSingleHomework(Long id, HomeworkDTO homeworkDTO) throws NonexistingEntityException, InvalidOperationException, InvalidEntityDataException;

    ExamExposeDTO addSingleExam(Long id, CreateExamDTO exam) throws NonexistingEntityException, InvalidOperationException, InvalidEntityDataException;

    List<ExamExposeDTO> getAllStudentExam(Long id) throws InvalidOperationException;

    HomeworkDTO addSingleHomeworkById(Long studentId, Long homeworkId) throws NonexistingEntityException, InvalidOperationException;

    ExamExposeDTO addSingleExamById(Long studentId, Long examId) throws NonexistingEntityException, InvalidOperationException;

    HomeworkDTO deleteSingleHomeworkById(Long id, Long homeworkId) throws NonexistingEntityException, InvalidOperationException;

    ExamExposeDTO deleteSingleExamById(Long id, Long examId) throws NonexistingEntityException, InvalidOperationException;

    List<ParentExposeDTO> getAllStudentParents(Long id) throws InvalidOperationException;

    StudentExposeDTO addStudentParent(Long studentId, Long parentId) throws NonexistingEntityException, InvalidOperationException;

    StudentExposeDTO deleteStudentParent(Long studentId, Long parentId) throws NonexistingEntityException, InvalidOperationException;

    List<CompetitionExposeDTO> getAllStudentCompetition(Long id) throws InvalidOperationException;

    CompetitionExposeDTO addCompetition(Long studentId, Long competitionId) throws NonexistingEntityException, InvalidOperationException;

    CompetitionExposeDTO removeCompetition(Long studentId, Long competitionId) throws NonexistingEntityException, InvalidOperationException;

    String getStudentsCount();

}
