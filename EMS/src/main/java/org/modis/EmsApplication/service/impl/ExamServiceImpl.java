package org.modis.EmsApplication.service.impl;

import org.modelmapper.ModelMapper;
import org.modis.EmsApplication.dao.ExamRepository;
import org.modis.EmsApplication.dao.SchoolYearRepository;
import org.modis.EmsApplication.dto.*;
import org.modis.EmsApplication.exception.InvalidEntityDataException;
import org.modis.EmsApplication.exception.InvalidOperationException;
import org.modis.EmsApplication.exception.NonexistingEntityException;
import org.modis.EmsApplication.model.Exam;
import org.modis.EmsApplication.model.Student;
import org.modis.EmsApplication.model.User;
import org.modis.EmsApplication.model.enums.Subject;
import org.modis.EmsApplication.service.ExamService;
import org.modis.EmsApplication.service.StudentService;
import org.modis.EmsApplication.service.UserService;
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
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final SchoolYearRepository schoolYearRepository;
    private final UserService userService;
    private final ModelMapper mapper = new ModelMapper();
    @Value("${environments.init-data.value}")
    public Boolean _initDB;


    @Autowired
    public ExamServiceImpl(ExamRepository examRepository, SchoolYearRepository schoolYearRepository, UserService userService) {
        this.examRepository = examRepository;
        this.schoolYearRepository = schoolYearRepository;
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ExamExposeDTO> getAllExams() {
        return examRepository.findAll().stream().map(exam -> mapper.map(exam, ExamExposeDTO.class)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ExamExposeDTO getExamById(Long id) throws NonexistingEntityException {
        return examRepository.findById(id).map(exam -> mapper.map(exam, ExamExposeDTO.class)).orElseThrow(() -> new NonexistingEntityException(String.format(CommonMessages.EXAM_DOES_NOT_EXISTS, id)));
    }

    public Exam getExamByIdModel(Long id) throws NonexistingEntityException {
        return examRepository.findById(id).orElseThrow(() -> new NonexistingEntityException(String.format(CommonMessages.EXAM_DOES_NOT_EXISTS, id)));
    }

    @Override
    public ExamExposeDTO create(CreateExamDTO examDTO) throws InvalidEntityDataException {
        Exam exam = mapper.map(examDTO, Exam.class);
        exam.setId(null);
        return mapper.map(examRepository.save(exam), ExamExposeDTO.class);
    }

    @Override
    public Exam createModel(CreateExamDTO examDTO) throws InvalidEntityDataException {
        Exam exam = mapper.map(examDTO, Exam.class);
        exam.setId(null);
        return examRepository.save(exam);
    }

    @Override
    public ExamExposeDTO update(Long id, UpdateExamDTO examDTO) throws InvalidEntityDataException, NonexistingEntityException {
        Exam oldExam = getExamByIdModel(id);
        oldExam.setSubject(examDTO.getSubject());
        oldExam.setContent(examDTO.getQuestions());
        oldExam.setModified(LocalDateTime.now());
        return mapper.map(examRepository.save(oldExam), ExamExposeDTO.class);
    }

    @Override
    public ExamExposeDTO deleteById(Long id) throws NonexistingEntityException {
        Exam exam = getExamByIdModel(id);
        schoolYearRepository.findAll().forEach(schoolYear -> schoolYear.getExams().remove(exam));
        examRepository.deleteStudentExam(exam.getId());
        examRepository.delete(exam);
        return mapper.map(exam, ExamExposeDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public String examsCount() {
        return String.valueOf(examRepository.count());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<UserExposeDTO, String> getAllPerformers(Long examId) {
        Exam exam = getExamByIdModel(examId);
        Map<User, String> models = parseExamPerformers(exam);
        Map<UserExposeDTO, String> exposeDTOs = new HashMap<>();
        for (Map.Entry<User, String> userStringEntry : models.entrySet()) {
            exposeDTOs.put(mapper.map(userStringEntry.getKey(), UserExposeDTO.class), userStringEntry.getValue());
        }
        return exposeDTOs;
    }

    private Map<User, String> parseExamPerformers(Exam exam) {
        Map<Student, String> examPerformances = exam.getSubmittedStudentAnswers();
        return new HashMap<>(examPerformances);
    }

    @Override
    @Transactional(readOnly = true)
    public AbstractMap.SimpleEntry<Long, String> getPerformerById(Long examId, Long studentId) {
        Exam exam = getExamByIdModel(examId);
        var performerEntry = parseExamPerformers(exam).entrySet().stream().filter(entry -> entry.getKey().getId().equals(studentId)).findFirst().orElseThrow(() -> new NonexistingEntityException(String.format(CommonMessages.MISSING_USER_ANSWER, exam.getId(), studentId)));
        return new AbstractMap.SimpleEntry<>(performerEntry.getKey().getId(), performerEntry.getValue());
    }

    @Override
    @PreAuthorize("#studentId == authentication.principal.id or hasRole('ROLE_HEADMASTER')")
    public ExamExposeDTO performExam(Long studentId, Long examId, String answers) throws NonexistingEntityException, InvalidOperationException, InvalidEntityDataException {
        Exam exam = getExamByIdModel(examId);
        User user = userService.getUserByIdModel(studentId);
        if (!(user instanceof Student)) {
            throw new InvalidOperationException(CommonMessages.NON_STUDENT_TRY_TO_PERFORM_EXAM);
        }
        if (!((Student) user).getExams().contains(exam)) {
            throw new NonexistingEntityException(String.format(CommonMessages.EXAM_NOT_ASSIGNED_TO_STUDENT, exam.getId(), user.getId()));
        }
        exam.getSubmittedStudentAnswers().put((Student) user, answers);
        exam.setModified(LocalDateTime.now());
        ((Student) user).getExams().remove(exam);
        user.setModified(LocalDateTime.now());
        examRepository.save(exam);
        userService.updateModel(user);
        return mapper.map(exam, ExamExposeDTO.class);

    }

    @Override
    public ExamExposeDTO gradeExamResult(Long studentId, Long examId, String grade) throws NonexistingEntityException, InvalidOperationException, InvalidEntityDataException {
        Exam exam = getExamByIdModel(examId);
        User student = userService.getUserByIdModel(studentId);
        if (!(student instanceof Student)) {
            throw new InvalidOperationException(CommonMessages.CAN_NOT_PERFORM_THIS_OPERATION_ON_NON_STUDENT);
        }
        if (parseExamPerformers(exam).keySet().stream().noneMatch(user -> user.getId().equals(studentId))) {
            throw new NonexistingEntityException(String.format(CommonMessages.USER_IS_NOT_EXAM_PERFORMER, student.getId(), exam.getId()));
        }
        if (exam.getResults().keySet().stream().anyMatch(user -> user.getId().equals(studentId)) && !((Student) student).getExams().contains(exam)) {
            Double lastGrade = getStudentExamResults(exam.getId(), student.getId()).getValue();
            throw new InvalidOperationException(String.format(CommonMessages.USER_ALREADY_WAS_GRADED_FOR_THIS_EXAM, student.getId(), exam.getId(), lastGrade));
        }
        BigDecimal providedGrade = new BigDecimal(grade);
        if (providedGrade.compareTo(BigDecimal.valueOf(2.00)) < 0 || providedGrade.compareTo(BigDecimal.valueOf(6.00)) > 0) {
            throw new InvalidEntityDataException(CommonMessages.GRADE_MUST_BE_BETWEEN_TWO_AND_SIX);
        }
        exam.getResults().put((Student) student, providedGrade.doubleValue());
        ((Student) student).getGrades().putIfAbsent(exam.getSubject(), "");
        ((Student) student).getGrades().put(exam.getSubject(), ((Student) student).getGrades().get(exam.getSubject()).concat(providedGrade.toString()).concat(GRADE_SEPARATOR));
        student.setModified(LocalDateTime.now());
        exam.setModified(LocalDateTime.now());
        examRepository.save(exam);
        userService.updateModel(student);
        return mapper.map(exam, ExamExposeDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<StudentExposeDTO, Double> getAllExamResults(Long examId) throws NonexistingEntityException {
        Exam exam = getExamByIdModel(examId);
        Map<Student, Double> models = exam.getResults();
        Map<StudentExposeDTO, Double> exposeDTOs = new HashMap<>();
        for (Map.Entry<Student, Double> studentDoubleEntry : models.entrySet()) {
            exposeDTOs.put(StudentExposeDTO.parseModel(studentDoubleEntry.getKey()), studentDoubleEntry.getValue());
        }
        return exposeDTOs;
    }

    @Override
    @Transactional(readOnly = true)
    public Map.Entry<UserExposeDTO, Double> getStudentExamResults(Long examId, Long studentId) throws NonexistingEntityException {
        Exam exam = getExamByIdModel(examId);
        User user = userService.getUserByIdModel(studentId);
        if (!(user instanceof Student)) {
            throw new InvalidEntityDataException(String.format(CommonMessages.INVALID_USER_TYPE, user.getId(), Student.class.getSimpleName()));
        }
        var entryExamResult = exam.getResults().entrySet().stream().filter(studentResult -> studentResult.getKey().equals(user)).findFirst().orElseThrow(() -> new NonexistingEntityException(String.format(CommonMessages.MISSING_STUDENT_RESULT, exam.getId(), studentId)));

        return new AbstractMap.SimpleEntry<>(mapper.map(entryExamResult.getKey(), UserExposeDTO.class), entryExamResult.getValue());
    }

    @Override
    public Map<StudentExposeDTO, Double> updateExamResult(Long examId, Long userId, String grade) throws NonexistingEntityException, InvalidEntityDataException {
        User student = userService.getUserByIdModel(userId);
        Exam exam = getExamByIdModel(examId);
        if (!(student instanceof Student)) {
            throw new InvalidEntityDataException(String.format(CommonMessages.INVALID_USER_TYPE, student.getId(), Student.class.getSimpleName()));
        }
        if (!exam.getResults().containsKey(student)) {
            throw new NonexistingEntityException(String.format(CommonMessages.USER_MISSING_FROM_EXAM_RESULTS, student.getId(), exam.getId()));
        }

        Double oldGrade = exam.getResults().get(student);
        exam.getResults().put((Student) student, Double.parseDouble(grade));
        List<Double> newGrades = Arrays.stream(((Student) student).getGrades().get(exam.getSubject()).split(GRADE_SEPARATOR))
                .map(Double::parseDouble).collect(Collectors.toList());
        newGrades.remove(oldGrade);
        newGrades.add(Double.parseDouble(grade));
        ((Student) student).getGrades().put(exam.getSubject(), newGrades.stream().map(String::valueOf).collect(Collectors.joining(GRADE_SEPARATOR)));

        userService.updateModel(student);
        examRepository.updateExam(Double.parseDouble(grade), exam.getId(), student.getId());

        Map<StudentExposeDTO, Double> exposeDTOs = new HashMap<>();
        Map<Student, Double> models = exam.getResults();
        for (Map.Entry<Student, Double> studentDoubleEntry : models.entrySet()) {
            exposeDTOs.put(StudentExposeDTO.parseModel(studentDoubleEntry.getKey()), studentDoubleEntry.getValue());
        }
        return exposeDTOs;
    }

    private Map<Subject, List<BigDecimal>> getStudentGrades(Student user) {
        Map<Subject, List<BigDecimal>> grades = new LinkedHashMap<>();
        for (Map.Entry<Subject, String> subjectGrades : user.getGrades().entrySet()) {
            grades.put(subjectGrades.getKey(), Arrays.stream(subjectGrades.getValue().split(GRADE_SEPARATOR)).map(BigDecimal::new).collect(Collectors.toList()));
        }
        return grades;
    }

    @Override
    public Map<StudentExposeDTO, Double> removeExamResult(Long examId, Long userId) throws NonexistingEntityException, InvalidEntityDataException {
        Exam exam = getExamByIdModel(examId);
        User student = userService.getUserByIdModel(userId);
        if (exam.getResults().keySet().stream().noneMatch(user -> user.getId().equals(student.getId()))) {
            throw new NonexistingEntityException(String.format(CommonMessages.USER_MISSING_FROM_EXAM_RESULTS, student.getId(), exam.getId()));
        }
        Map<Student, Double> newResultMap = new HashMap<>();
        for (Map.Entry<Student, Double> userDoubleEntry : exam.getResults().entrySet()) {
            if (!userDoubleEntry.getKey().getId().equals(student.getId())) {
                newResultMap.put(userDoubleEntry.getKey(), userDoubleEntry.getValue());
            }
        }
        Map<Student, Double> models = exam.getResults();
        Map<StudentExposeDTO, Double> exposeDTOs = new HashMap<>();
        exam.setResults(newResultMap);
        for (Map.Entry<Student, Double> studentDoubleEntry : models.entrySet()) {
            exposeDTOs.put(StudentExposeDTO.parseModel(studentDoubleEntry.getKey()), studentDoubleEntry.getValue());
        }
        examRepository.save(exam);
        userService.updateModel(student);
        return exposeDTOs;
    }


}
