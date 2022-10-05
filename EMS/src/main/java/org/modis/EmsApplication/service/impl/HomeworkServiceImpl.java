package org.modis.EmsApplication.service.impl;

import org.modelmapper.ModelMapper;
import org.modis.EmsApplication.dao.HomeworkRepository;
import org.modis.EmsApplication.dto.HomeworkDTO;
import org.modis.EmsApplication.dto.StudentExposeDTO;
import org.modis.EmsApplication.exception.InvalidEntityDataException;
import org.modis.EmsApplication.exception.NonexistingEntityException;
import org.modis.EmsApplication.model.Homework;
import org.modis.EmsApplication.model.Student;
import org.modis.EmsApplication.model.User;
import org.modis.EmsApplication.service.HomeworkService;
import org.modis.EmsApplication.service.UserService;
import org.modis.EmsApplication.utils.CommonMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static org.modis.EmsApplication.utils.CommonMessages.GRADE_SEPARATOR;

@Service
@Transactional
public class HomeworkServiceImpl implements HomeworkService {

    private final HomeworkRepository homeworkRepository;
    private final UserService userService;
    private final ModelMapper mapper = new ModelMapper();

    @Autowired
    public HomeworkServiceImpl(HomeworkRepository homeworkRepository, UserService userService) {
        this.homeworkRepository = homeworkRepository;
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<HomeworkDTO> getAllHomework() {
        return homeworkRepository.findAll().stream().map(homework -> mapper.map(homework, HomeworkDTO.class)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public HomeworkDTO getHomeworkById(Long id) throws NonexistingEntityException {
        return homeworkRepository.findById(id).map(homework -> mapper.map(homework, HomeworkDTO.class)).orElseThrow(() -> new NonexistingEntityException(
                String.format(CommonMessages.HOMEWORK_DOES_NOT_EXISTS, id)
        ));
    }

    public Homework getHomeworkByIdModel(Long id) throws NonexistingEntityException {
        return homeworkRepository.findById(id).orElseThrow(() -> new NonexistingEntityException(
                String.format(CommonMessages.HOMEWORK_DOES_NOT_EXISTS, id)
        ));
    }

    @Override
    public HomeworkDTO create(HomeworkDTO homeworkDTO) throws InvalidEntityDataException {
        Homework homework = mapper.map(homeworkDTO, Homework.class);
        homework.setId(null);
        return mapper.map(homeworkRepository.save(homework), HomeworkDTO.class);
    }

    @Override
    public Homework createModel(HomeworkDTO homeworkDTO) throws InvalidEntityDataException {
        Homework homework = mapper.map(homeworkDTO, Homework.class);
        homework.setId(null);
        return homeworkRepository.save(homework);
    }

    @Override
    public HomeworkDTO update(Long homeworkId, HomeworkDTO newHomeworkInformation) throws InvalidEntityDataException, NonexistingEntityException {
        Homework homework = getHomeworkByIdModel(homeworkId);
        homework.setDescription(newHomeworkInformation.getDescription());
        homework.setSubject(newHomeworkInformation.getSubject());
        homework.setModified(LocalDateTime.now());
        return mapper.map(homeworkRepository.save(homework), HomeworkDTO.class);
    }

    @Override
    public HomeworkDTO deleteById(Long id) throws NonexistingEntityException {
        Homework homework = getHomeworkByIdModel(id);
        homeworkRepository.cleanStudentHomework(homework.getId());
        homeworkRepository.cleanSchoolYearHomework(homework.getId());
        homeworkRepository.delete(homework);
        return mapper.map(homework, HomeworkDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<StudentExposeDTO, String> getAllHomeworkPerformers(Long id) {
        Homework homework = getHomeworkByIdModel(id);
        Map<Student, String> models = homework.getSubmittedStudentAnswers();
        Map<StudentExposeDTO, String> exposeDTOs = new HashMap<>();
        for (Map.Entry<Student, String> studentStringEntry : models.entrySet()) {
            exposeDTOs.put(StudentExposeDTO.parseModel(studentStringEntry.getKey()), studentStringEntry.getValue());
        }
        return exposeDTOs;
    }

    @Override
    @PreAuthorize("#studentId == authentication.principal.id or hasRole('ROLE_HEADMASTER')")
    public HomeworkDTO submitHomework(Long homeworkId, Long studentId, String answer) {
        Homework homework = getHomeworkByIdModel(homeworkId);
        User student = userService.getUserByIdModel(studentId);
        if (student instanceof Student) {
            if (!((Student) student).getHomework().contains(homework)) {
                throw new NonexistingEntityException(String.format(CommonMessages.HOMEWORK_NOT_ASSIGNED_TO_THE_USER, homeworkId, student.getId()));
            }
            ((Student) student).getHomework().remove(homework);
            homework.getSubmittedStudentAnswers().put((Student) student, answer);
            homework.setModified(LocalDateTime.now());
            userService.updateModel(student);
            homeworkRepository.save(homework);
            return mapper.map(homework, HomeworkDTO.class);
        } else {
            throw new InvalidEntityDataException(String.format(CommonMessages.INVALID_USER_TYPE, studentId, Student.class.getSimpleName()));
        }
    }

    @Override
    public StudentExposeDTO gradeHomework(Long homeworkId, Long studentId, String grade) {
        Homework homework = getHomeworkByIdModel(homeworkId);
        Student student = (Student) userService.getUserByIdModel(studentId);
        if (!homework.getSubmittedStudentAnswers().containsKey(student)) {
            throw new NonexistingEntityException(String.format(CommonMessages.HOMEWORK_NOT_POSSESS_STUDENT_RESULT, homeworkId, student.getId()));
        }
        student.getGrades().putIfAbsent(homework.getSubject(), "");
        String currentGrades = student.getGrades().get(homework.getSubject());
        student.getGrades().put(homework.getSubject(), currentGrades.concat(grade).concat(GRADE_SEPARATOR));
        userService.updateModel(student);
        return StudentExposeDTO.parseModel(student);
    }

    @Override
    @Transactional(readOnly = true)
    public String homeworkCount() {
        return String.valueOf(homeworkRepository.count());
    }
}
