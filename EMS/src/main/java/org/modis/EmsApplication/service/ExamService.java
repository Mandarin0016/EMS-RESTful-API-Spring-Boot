package org.modis.EmsApplication.service;

import org.modis.EmsApplication.dto.*;
import org.modis.EmsApplication.exception.InvalidEntityDataException;
import org.modis.EmsApplication.exception.InvalidOperationException;
import org.modis.EmsApplication.exception.NonexistingEntityException;
import org.modis.EmsApplication.model.Exam;
import org.modis.EmsApplication.model.Student;
import org.modis.EmsApplication.model.User;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ExamService {
    Collection<ExamExposeDTO> getAllExams();

    ExamExposeDTO getExamById(Long id) throws NonexistingEntityException;

    Exam getExamByIdModel(Long id) throws NonexistingEntityException;

    ExamExposeDTO create(CreateExamDTO exam) throws InvalidEntityDataException;

    Exam createModel(CreateExamDTO exam) throws InvalidEntityDataException;

    ExamExposeDTO update(Long id, UpdateExamDTO exam) throws InvalidEntityDataException, NonexistingEntityException;

    ExamExposeDTO deleteById(Long id) throws NonexistingEntityException;

    String examsCount();

    Map<UserExposeDTO, String> getAllPerformers(Long examId);

    AbstractMap.SimpleEntry<Long, String> getPerformerById(Long examId, Long studentId);

    ExamExposeDTO performExam(Long studentId, Long examId, String answer) throws NonexistingEntityException, InvalidOperationException, InvalidEntityDataException;

    Map<StudentExposeDTO, Double> getAllExamResults(Long examId) throws NonexistingEntityException, InvalidOperationException, InvalidEntityDataException;

    Map.Entry<UserExposeDTO, Double> getStudentExamResults(Long examId, Long studentId) throws NonexistingEntityException, InvalidOperationException, InvalidEntityDataException;

    ExamExposeDTO gradeExamResult(Long studentId, Long examId, String grade) throws NonexistingEntityException, InvalidOperationException, InvalidEntityDataException;

    Map<StudentExposeDTO, Double> updateExamResult(Long examId, Long userId, String grade) throws NonexistingEntityException, InvalidOperationException, InvalidEntityDataException;

    Map<StudentExposeDTO, Double> removeExamResult(Long examId, Long userId) throws NonexistingEntityException, InvalidOperationException, InvalidEntityDataException;
}
