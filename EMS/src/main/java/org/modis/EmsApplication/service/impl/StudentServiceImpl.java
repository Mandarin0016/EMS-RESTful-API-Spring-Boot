package org.modis.EmsApplication.service.impl;

import org.modelmapper.ModelMapper;
import org.modis.EmsApplication.dto.*;
import org.modis.EmsApplication.exception.InvalidEntityDataException;
import org.modis.EmsApplication.exception.InvalidOperationException;
import org.modis.EmsApplication.exception.NonexistingEntityException;
import org.modis.EmsApplication.model.*;
import org.modis.EmsApplication.model.enums.Subject;
import org.modis.EmsApplication.service.*;
import org.modis.EmsApplication.utils.CommonMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.modis.EmsApplication.utils.CommonMessages.GRADE_SEPARATOR;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final UserService userService;
    private final HomeworkService homeworkService;
    private final ExamService examService;
    private final CompetitionService competitionService;
    private final ModelMapper mapper = new ModelMapper();

    @Autowired
    public StudentServiceImpl(UserService userService, HomeworkService homeworkService, ExamService examService, CompetitionService competitionService) {
        this.userService = userService;
        this.homeworkService = homeworkService;
        this.examService = examService;
        this.competitionService = competitionService;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ROLE_HEADMASTER') or hasRole('ROLE_TEACHER')")
    public Collection<StudentExposeDTO> getAllStudents() {
        return getAllStudentsModels().stream().map(student -> StudentExposeDTO.parseModel((Student) student)).toList();
    }

    public Collection<User> getAllStudentsModels() {
        return userService.getAllUsersModels().stream().filter(user -> user instanceof Student).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public StudentExposeDTO getStudentById(Long id) {
        return StudentExposeDTO.parseModel((Student) userService.getUserByIdModel(id));
    }

    @Override
    public User getStudentByIdModel(Long id) {
        return getAllStudentsModels().stream().filter(student -> student.getId().equals(id)).findFirst().orElseThrow(() -> new NonexistingEntityException(String.format(CommonMessages.STUDENT_DOES_NOT_EXISTS, id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Subject, List<BigDecimal>> getAllStudentGrades(Long id) throws NonexistingEntityException, InvalidOperationException {
        User user = userService.getUserByIdModel(id);
        if (!(user instanceof Student)) {
            throw new InvalidOperationException(String.format(CommonMessages.INVALID_USER_TYPE, id, Student.class.getSimpleName()));
        } else {
            return getStudentGrades((Student) user);
        }
    }

    @Override
    @PreAuthorize("hasRole('ROLE_HEADMASTER') or hasRole('ROLE_TEACHER')")
    public Map<Subject, List<BigDecimal>> addStudentGrade(Long id, String subjectName, String grade) throws NonexistingEntityException, InvalidOperationException {
        Subject subject = Subject.valueOf(subjectName);
        User user = userService.getUserByIdModel(id);
        BigDecimal providedGrade = new BigDecimal(grade);

        if (providedGrade.compareTo(BigDecimal.valueOf(2.00)) < 0 || providedGrade.compareTo(BigDecimal.valueOf(6.00)) > 0) {
            throw new InvalidEntityDataException(CommonMessages.GRADE_MUST_BE_BETWEEN_TWO_AND_SIX);
        } else {
            if (user instanceof Student) {
                if (((Student) user).getSchoolYear() == null) {
                    throw new InvalidEntityDataException(String.format(CommonMessages.STUDENT_DOES_NOT_HAVE_SCHOOLYEAR, user.getId()));
                }
                ((Student) user).getGrades().putIfAbsent(subject, "");
                List<String> currentGrades = Arrays.stream(((Student) user).getGrades().get(subject).split(GRADE_SEPARATOR)).collect(Collectors.toList());
                currentGrades.add(String.valueOf(providedGrade));
                currentGrades.removeIf(String::isEmpty);
                ((Student) user).getGrades().put(subject, currentGrades.stream().map(String::valueOf).collect(Collectors.joining(GRADE_SEPARATOR)));
                user.setModified(LocalDateTime.now());
                userService.updateModel(user);
            } else {
                throw new InvalidOperationException(CommonMessages.CAN_NOT_GRADE_NON_STUDENT);
            }
        }
        return getStudentGrades((Student) user);
    }

    private Map<Subject, List<BigDecimal>> getStudentGrades(Student user) {
        Map<Subject, List<BigDecimal>> grades = new HashMap<>();
        for (Map.Entry<Subject, String> subjectGrades : user.getGrades().entrySet()) {
            if (subjectGrades.getValue().trim().isEmpty()) {
                grades.put(subjectGrades.getKey(), new ArrayList<>());
                continue;
            }
            grades.put(subjectGrades.getKey(), Arrays.stream(subjectGrades.getValue().split(GRADE_SEPARATOR)).map(BigDecimal::new).collect(Collectors.toList()));
        }
        return grades;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_HEADMASTER') or hasRole('ROLE_TEACHER')")
    public Map<Subject, List<BigDecimal>> deleteStudentGrade(Long id, String subjectName, String grade) throws NonexistingEntityException, InvalidOperationException {
        Subject subject = Subject.valueOf(subjectName);
        User user = userService.getUserByIdModel(id);
        BigDecimal providedGrade = new BigDecimal(grade);
        if (user instanceof Student) {
            if (!((Student) user).getGrades().containsKey(subject)) {
                throw new NonexistingEntityException(String.format(CommonMessages.SUCH_SUBJECT_CAN_NOT_BE_FOUND, subject, user.getId()));
            }
            List<String> studentGrades = Arrays.stream(((Student) user).getGrades().get(subject).split(GRADE_SEPARATOR)).collect(Collectors.toList());
            for (String studentGrade : studentGrades) {
                if (studentGrade.equals(String.valueOf(providedGrade))) {
                    studentGrades.remove(studentGrade);
                    ((Student) user).getGrades().put(subject, studentGrades.stream().map(String::valueOf).collect(Collectors.joining(GRADE_SEPARATOR)));
                    user.setModified(LocalDateTime.now());
                    userService.updateModel(user);
                    return getStudentGrades((Student) user);
                }
            }
        } else {
            throw new InvalidOperationException(CommonMessages.CAN_NOT_PERFORM_THIS_OPERATION_ON_NON_STUDENT);
        }
        throw new NonexistingEntityException(String.format(CommonMessages.SUCH_GRADE_CAN_NOT_BE_FOUND, providedGrade.doubleValue(), user.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HomeworkDTO> getAllStudentHomework(Long id) throws InvalidOperationException, NonexistingEntityException {
        User user = userService.getUserByIdModel(id);
        if (!(user instanceof Student)) {
            throw new InvalidOperationException(String.format(CommonMessages.INVALID_USER_TYPE, id, Student.class.getSimpleName()));
        } else {
            return ((Student) user).getHomework().stream().map(homework -> mapper.map(homework, HomeworkDTO.class)).toList();
        }
    }

    @Override
    @PreAuthorize("hasRole('ROLE_HEADMASTER') or hasRole('ROLE_TEACHER')")
    public HomeworkDTO addSingleHomework(Long id, HomeworkDTO homeworkDTO) throws NonexistingEntityException, InvalidOperationException, InvalidEntityDataException {
        User user = userService.getUserByIdModel(id);
        Homework createdHomework;
        if (user instanceof Student) {
            if (((Student) user).getSchoolYear() == null) {
                throw new InvalidEntityDataException(String.format(CommonMessages.STUDENT_DOES_NOT_HAVE_SCHOOLYEAR, user.getId()));
            }
            createdHomework = homeworkService.createModel(homeworkDTO);
            ((Student) user).getHomework().add(createdHomework);
            user.setModified(LocalDateTime.now());
        } else {
            throw new InvalidOperationException(CommonMessages.CAN_NOT_PERFORM_THIS_OPERATION_ON_NON_STUDENT);
        }
        return mapper.map(createdHomework, HomeworkDTO.class);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_HEADMASTER') or hasRole('ROLE_TEACHER')")
    public ExamExposeDTO addSingleExam(Long id, CreateExamDTO examDTO) throws NonexistingEntityException, InvalidOperationException, InvalidEntityDataException {
        User user = userService.getUserByIdModel(id);
        Exam createdExam;
        if (user instanceof Student) {
            if (((Student) user).getSchoolYear() == null) {
                throw new InvalidEntityDataException(String.format(CommonMessages.STUDENT_DOES_NOT_HAVE_SCHOOLYEAR, user.getId()));
            }
            createdExam = examService.createModel(examDTO);
            ((Student) user).getExams().add(createdExam);
            user.setModified(LocalDateTime.now());
            userService.updateModel(user);
        } else {
            throw new InvalidOperationException(CommonMessages.CAN_NOT_PERFORM_THIS_OPERATION_ON_NON_STUDENT);
        }
        return mapper.map(createdExam, ExamExposeDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamExposeDTO> getAllStudentExam(Long id) throws InvalidOperationException {
        User user = userService.getUserByIdModel(id);
        if (!(user instanceof Student)) {
            throw new InvalidOperationException(String.format(CommonMessages.INVALID_USER_TYPE, id, Student.class.getSimpleName()));
        } else {
            return ((Student) user).getExams().stream().map(exam -> mapper.map(exam, ExamExposeDTO.class)).toList();
        }
    }

    @Override
    @PreAuthorize("hasRole('ROLE_HEADMASTER') or hasRole('ROLE_TEACHER')")
    public HomeworkDTO addSingleHomeworkById(Long studentId, Long homeworkId) throws NonexistingEntityException, InvalidOperationException {
        User user = userService.getUserByIdModel(studentId);
        Homework existingHomework = homeworkService.getHomeworkByIdModel(homeworkId);
        if (user instanceof Student) {
            if (((Student) user).getSchoolYear() == null) {
                throw new InvalidEntityDataException(String.format(CommonMessages.STUDENT_DOES_NOT_HAVE_SCHOOLYEAR, user.getId()));
            }
            if (((Student) user).getHomework().contains(existingHomework)) {
                throw new InvalidOperationException(String.format(CommonMessages.USER_ALREADY_POSSESS_THIS_RESOURCE, user.getId()));
            }
            ((Student) user).getHomework().add(existingHomework);
            user.setModified(LocalDateTime.now());
        } else {
            throw new InvalidOperationException(CommonMessages.CAN_NOT_PERFORM_THIS_OPERATION_ON_NON_STUDENT);
        }
        userService.updateModel(user);
        return mapper.map(existingHomework, HomeworkDTO.class);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_HEADMASTER') or hasRole('ROLE_TEACHER')")
    public ExamExposeDTO addSingleExamById(Long studentId, Long examId) throws NonexistingEntityException, InvalidOperationException {
        User user = userService.getUserByIdModel(studentId);
        Exam existingExam = examService.getExamByIdModel(examId);
        if (user instanceof Student) {
            if (((Student) user).getSchoolYear() == null) {
                throw new InvalidEntityDataException(String.format(CommonMessages.STUDENT_DOES_NOT_HAVE_SCHOOLYEAR, user.getId()));
            }
            if (((Student) user).getExams().contains(existingExam)) {
                throw new InvalidOperationException(String.format(CommonMessages.USER_ALREADY_POSSESS_THIS_RESOURCE, user.getId()));
            }
            ((Student) user).getExams().add(existingExam);
            user.setModified(LocalDateTime.now());
        } else {
            throw new InvalidOperationException(CommonMessages.CAN_NOT_PERFORM_THIS_OPERATION_ON_NON_STUDENT);
        }
        userService.updateModel(user);
        return mapper.map(existingExam, ExamExposeDTO.class);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_HEADMASTER') or hasRole('ROLE_TEACHER')")
    public HomeworkDTO deleteSingleHomeworkById(Long id, Long homeworkId) throws NonexistingEntityException, InvalidOperationException {
        User user = userService.getUserByIdModel(id);
        Homework existingHomework = homeworkService.getHomeworkByIdModel(homeworkId);
        if (user instanceof Student) {
            if (!((Student) user).getHomework().contains(existingHomework)) {
                throw new NonexistingEntityException(String.format(CommonMessages.HOMEWORK_NOT_ASSIGNED_TO_THE_USER, existingHomework.getId(), user.getId()));
            }
            ((Student) user).getHomework().remove(existingHomework);
            user.setModified(LocalDateTime.now());
        } else {
            throw new InvalidOperationException(CommonMessages.CAN_NOT_PERFORM_THIS_OPERATION_ON_NON_STUDENT);
        }
        userService.updateModel(user);
        return mapper.map(existingHomework, HomeworkDTO.class);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_HEADMASTER') or hasRole('ROLE_TEACHER')")
    public ExamExposeDTO deleteSingleExamById(Long id, Long examId) throws NonexistingEntityException, InvalidOperationException {
        User user = userService.getUserByIdModel(id);
        Exam existingExam = examService.getExamByIdModel(examId);
        if (user instanceof Student) {
            if (!((Student) user).getExams().contains(existingExam)) {
                throw new NonexistingEntityException(String.format(CommonMessages.EXAM_NOT_ASSIGNED_TO_THE_USER, existingExam.getId(), user.getId()));
            }
            ((Student) user).getExams().remove(existingExam);
            user.setModified(LocalDateTime.now());
        } else {
            throw new InvalidOperationException(CommonMessages.CAN_NOT_PERFORM_THIS_OPERATION_ON_NON_STUDENT);
        }
        userService.updateModel(user);
        return mapper.map(existingExam, ExamExposeDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParentExposeDTO> getAllStudentParents(Long id) throws InvalidOperationException {
        User user = userService.getUserByIdModel(id);
        if (!(user instanceof Student)) {
            throw new InvalidOperationException(String.format(CommonMessages.INVALID_USER_TYPE, id, Student.class.getSimpleName()));
        } else {
            return userService.getAllUsersModels().stream().filter(model -> ((Student) user).getParentsId().contains(model.getId())).map(model -> ParentExposeDTO.parseModel((Parent) model)).toList();
        }
    }

    @Override
    @PreAuthorize("hasRole('ROLE_HEADMASTER') or hasRole('ROLE_TEACHER')")
    public StudentExposeDTO addStudentParent(Long studentId, Long parentId) throws NonexistingEntityException, InvalidOperationException {
        User student = userService.getUserByIdModel(studentId);
        User parent = userService.getUserByIdModel(parentId);
        if (student instanceof Student && parent instanceof Parent) {
            if (((Student) student).getParentsId().contains(parentId)) {
                throw new InvalidOperationException(String.format(CommonMessages.USER_ALREADY_POSSESS_THIS_RESOURCE, student.getId()));
            }
            ((Student) student).getParentsId().add(parentId);
            student.setModified(LocalDateTime.now());
            ((Parent) parent).getChildren().add((Student) student);
            parent.setModified(LocalDateTime.now());
        } else {
            throw new InvalidOperationException(CommonMessages.CAN_NOT_PERFORM_THIS_OPERATION_ON_NON_STUDENT);
        }
        userService.updateModel(parent);
        userService.updateModel(student);
        return StudentExposeDTO.parseModel((Student) student);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_HEADMASTER') or hasRole('ROLE_TEACHER')")
    public StudentExposeDTO deleteStudentParent(Long studentId, Long parentId) throws NonexistingEntityException, InvalidOperationException {
        User student = userService.getUserByIdModel(studentId);
        User parent = userService.getUserByIdModel(parentId);
        if (student instanceof Student && parent instanceof Parent) {
            ((Student) student).getParentsId().remove(parentId);
            student.setModified(LocalDateTime.now());
            ((Parent) parent).getChildren().remove(student);
            parent.setModified(LocalDateTime.now());
        } else {
            throw new InvalidOperationException(CommonMessages.CAN_NOT_PERFORM_THIS_OPERATION_ON_NON_STUDENT);
        }
        userService.updateModel(parent);
        userService.updateModel(student);
        return StudentExposeDTO.parseModel((Student) student);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompetitionExposeDTO> getAllStudentCompetition(Long id) throws InvalidOperationException {
        User user = userService.getUserByIdModel(id);
        if (!(user instanceof Student)) {
            throw new InvalidOperationException(String.format(CommonMessages.INVALID_USER_TYPE, id, Student.class.getSimpleName()));
        } else {
            return ((Student) user).getCompetitions().stream().map(competition -> mapper.map(competition, CompetitionExposeDTO.class)).toList();
        }
    }

    @Override
    @PreAuthorize("hasRole('ROLE_HEADMASTER') or hasRole('ROLE_TEACHER')")
    public CompetitionExposeDTO addCompetition(Long studentId, Long competitionId) throws NonexistingEntityException, InvalidOperationException {
        User student = userService.getUserByIdModel(studentId);
        Competition competition = competitionService.getCompetitionByIdModel(competitionId);
        if (student instanceof Student) {
            if (((Student) student).getSchoolYear() == null) {
                throw new InvalidEntityDataException(String.format(CommonMessages.STUDENT_DOES_NOT_HAVE_SCHOOLYEAR, student.getId()));
            }
            if (((Student) student).getCompetitions().contains(competition)) {
                throw new InvalidOperationException(String.format(CommonMessages.USER_ALREADY_POSSESS_THIS_RESOURCE, student.getId()));
            }
            if (Competition.isFinished(competition.getEndDate())) {
                throw new InvalidEntityDataException(String.format(CommonMessages.COMPETITION_FINISHED, competition.getId()));
            }
            ((Student) student).getCompetitions().add(competition);
            competition.getRegisteredStudent().add((Student) student);
            student.setModified(LocalDateTime.now());
            competition.setModified(LocalDateTime.now());
        } else {
            throw new InvalidOperationException(CommonMessages.CAN_NOT_PERFORM_THIS_OPERATION_ON_NON_STUDENT);
        }
        userService.updateModel(student);
        competitionService.updateModel(competition);
        return mapper.map(competition, CompetitionExposeDTO.class);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_HEADMASTER') or hasRole('ROLE_TEACHER')")
    public CompetitionExposeDTO removeCompetition(Long studentId, Long competitionId) throws NonexistingEntityException, InvalidOperationException {
        User student = userService.getUserByIdModel(studentId);
        Competition competition = competitionService.getCompetitionByIdModel(competitionId);
        if (student instanceof Student) {
            ((Student) student).getCompetitions().remove(competition);
            competition.getRegisteredStudent().remove(student);
            student.setModified(LocalDateTime.now());
            competition.setModified(LocalDateTime.now());
        } else {
            throw new InvalidOperationException(CommonMessages.CAN_NOT_PERFORM_THIS_OPERATION_ON_NON_STUDENT);
        }
        userService.updateModel(student);
        competitionService.updateModel(competition);
        return mapper.map(competition, CompetitionExposeDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public String getStudentsCount() {
        return String.valueOf(getAllStudents().size());
    }
}
