package org.modis.EmsApplication.service;

import org.modis.EmsApplication.dto.HomeworkDTO;
import org.modis.EmsApplication.dto.StudentExposeDTO;
import org.modis.EmsApplication.dto.UserExposeDTO;
import org.modis.EmsApplication.exception.InvalidEntityDataException;
import org.modis.EmsApplication.exception.NonexistingEntityException;
import org.modis.EmsApplication.model.Homework;
import org.modis.EmsApplication.model.Student;
import org.modis.EmsApplication.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public interface HomeworkService {

    Collection<HomeworkDTO> getAllHomework();

    HomeworkDTO getHomeworkById(Long id) throws NonexistingEntityException;

    Homework getHomeworkByIdModel(Long id) throws NonexistingEntityException;

    HomeworkDTO create(HomeworkDTO homeworkDTO) throws InvalidEntityDataException;

    Homework createModel(HomeworkDTO homeworkDTO) throws InvalidEntityDataException;

    HomeworkDTO update(Long homeworkId, HomeworkDTO homeworkDTO) throws InvalidEntityDataException, NonexistingEntityException;

    HomeworkDTO deleteById(Long id) throws NonexistingEntityException;

    Map<StudentExposeDTO, String> getAllHomeworkPerformers(Long id);

    HomeworkDTO submitHomework(Long homeworkId, Long studentId, String answer);

    StudentExposeDTO gradeHomework(Long homeworkId, Long studentId, String grade);

    String homeworkCount();
}
