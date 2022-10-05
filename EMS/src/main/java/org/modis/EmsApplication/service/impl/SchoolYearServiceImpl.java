package org.modis.EmsApplication.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modis.EmsApplication.dao.SchoolYearRepository;
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

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@Transactional
public class SchoolYearServiceImpl implements SchoolYearService {

    private final SchoolYearRepository schoolYearRepository;
    private final TimetableService timetableService;
    private final UserService userService;
    private final ExamService examService;
    private final HomeworkService homeworkService;
    private final ModelMapper mapper = new ModelMapper();

    @Autowired
    public SchoolYearServiceImpl(SchoolYearRepository schoolYearRepository, TimetableService timetableService, UserService userService, ExamService examService, HomeworkService homeworkService) {
        this.schoolYearRepository = schoolYearRepository;
        this.timetableService = timetableService;
        this.userService = userService;
        this.examService = examService;
        this.homeworkService = homeworkService;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<SchoolYearExposeDTO> getAllSchoolYears() {
        return schoolYearRepository.findAll().stream().map(SchoolYearExposeDTO::parseModel).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SchoolYearExposeDTO getSchoolYearById(Long id) throws NonexistingEntityException {
        return schoolYearRepository.findById(id).map(SchoolYearExposeDTO::parseModel).orElseThrow(() -> new NonexistingEntityException(String.format(CommonMessages.SCHOOLYEAR_DOES_NOT_EXISTS, id)));
    }


    private SchoolYear getSchoolYearByIdModel(Long id) throws NonexistingEntityException {
        return schoolYearRepository.findById(id).orElseThrow(() -> new NonexistingEntityException(String.format(CommonMessages.SCHOOLYEAR_DOES_NOT_EXISTS, id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Subject> getSchoolYearSubjects(Long id) throws NonexistingEntityException {
        return Collections.unmodifiableSet(getSchoolYearById(id).getSubjects());
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<StudentExposeDTO> getSchoolYearStudents(Long id) throws NonexistingEntityException {
        return Collections.unmodifiableCollection(getSchoolYearById(id).getStudents());
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<TeacherExposeDTO> getSchoolYearTeachers(Long id) throws NonexistingEntityException {
        return Collections.unmodifiableCollection(getSchoolYearById(id).getTeachers());
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ExamExposeDTO> getSchoolYearExams(Long id) throws NonexistingEntityException {
        return Collections.unmodifiableCollection(getSchoolYearById(id).getExams());
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<HomeworkDTO> getSchoolYearHomework(Long id) throws NonexistingEntityException {
        return Collections.unmodifiableCollection(getSchoolYearById(id).getHomework());
    }

    @Override
    @Transactional(readOnly = true)
    public TimetableDTO getSchoolYearTimetable(Long id) throws NonexistingEntityException {
        SchoolYear schoolYear = getSchoolYearByIdModel(id);
        return mapper.map(schoolYear.getTimetable(), TimetableDTO.class);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_HEADMASTER')")
    public StudentExposeDTO addStudent(Long schoolYearId, Long studentId) throws NonexistingEntityException, InvalidOperationException {
        SchoolYear schoolYear = getSchoolYearByIdModel(schoolYearId);
        User user = userService.getUserByIdModel(studentId);
        if (user instanceof Student) {
            if (schoolYear.getStudents().contains(user)) {
                throw new InvalidOperationException(String.format(CommonMessages.USER_ALREADY_ASSOCIATED_WITH_THIS_SCHOOLYEAR, user.getId(), schoolYear.getId()));
            }
            if (((Student) user).getSchoolYear() != null) {
                throw new InvalidOperationException(String.format(CommonMessages.STUDENT_ALREADY_ASSIGNED_TO_ANOTHER_SCHOOLYEAR, user.getId()));
            }
            schoolYear.getStudents().add((Student) user);
            ((Student) user).setSchoolYear(schoolYear);
            schoolYear.getExams().stream().filter(exam -> !(((Student) user).getExams().contains(exam))).forEach(exam -> ((Student) user).getExams().add(exam));
            schoolYear.getSubjects().stream().filter(subject -> !(((Student) user).getSubjects().contains(subject))).forEach(subject -> ((Student) user).getSubjects().add(subject));
            schoolYear.getHomework().stream().filter(homework -> !(((Student) user).getHomework().contains(homework))).forEach(homework -> ((Student) user).getHomework().add(homework));
            ((Student) user).setTimetable(schoolYear.getTimetable());
            user.setModified(LocalDateTime.now());
            schoolYear.setModified(LocalDateTime.now());
            userService.updateModel(user);
            schoolYearRepository.save(schoolYear);
        } else {
            throw new InvalidOperationException(CommonMessages.CANT_ADD_NON_STUDENT_TO_SCHOOLYEAR_STUDENTS_COLLECTION);
        }
        return StudentExposeDTO.parseModel((Student) user);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_HEADMASTER')")
    public TeacherExposeDTO addTeacher(Long schoolYearId, Long teacherId) throws NonexistingEntityException, InvalidOperationException {
        SchoolYear schoolYear = getSchoolYearByIdModel(schoolYearId);
        User user = userService.getUserByIdModel(teacherId);
        if (user instanceof Teacher) {
            if (schoolYear.getTeachers().contains(user)) {
                throw new InvalidOperationException(String.format(CommonMessages.USER_ALREADY_ASSOCIATED_WITH_THIS_SCHOOLYEAR, user.getId(), schoolYear.getId()));
            }
            schoolYear.getTeachers().add((Teacher) user);
            ((Teacher) user).getSchoolYears().add(schoolYear);
            user.setModified(LocalDateTime.now());
            schoolYear.setModified(LocalDateTime.now());
            userService.updateModel(user);
            schoolYearRepository.save(schoolYear);
        } else {
            throw new InvalidOperationException(CommonMessages.CANT_ADD_NON_TEACHER_TO_SCHOOLYEAR_TEACHERS_COLLECTION);
        }
        return TeacherExposeDTO.parseModel(user);
    }

    @Override
    public Subject addSubject(Long schoolYearId, Subject subject) throws NonexistingEntityException, InvalidOperationException {
        SchoolYear schoolYear = getSchoolYearByIdModel(schoolYearId);
        if (schoolYear.getSubjects().contains(subject)) {
            throw new InvalidOperationException(String.format(CommonMessages.SUBJECT_ALREADY_ASSOCIATED_WITH_THIS_SCHOOLYEAR, subject.name(), schoolYear.getId()));
        }
        schoolYear.getSubjects().add(subject);
        schoolYear.setModified(LocalDateTime.now());
        for (Student student : schoolYear.getStudents()) {
            if (!student.getSubjects().contains(subject)) {
                student.getSubjects().add(subject);
                userService.updateModel(student);
            }
        }
        schoolYearRepository.save(schoolYear);
        return subject;
    }

    @Override
    public ExamExposeDTO addExam(Long schoolYearId, Long examId) throws NonexistingEntityException, InvalidOperationException {
        SchoolYear schoolYear = getSchoolYearByIdModel(schoolYearId);
        Exam exam = examService.getExamByIdModel(examId);
        if (schoolYear.getExams().contains(exam)) {
            throw new InvalidOperationException(String.format(CommonMessages.EXAM_ALREADY_ASSOCIATED_WITH_THIS_SCHOOLYEAR, exam.getId(), schoolYear.getId()));
        }
        exam.setModified(LocalDateTime.now());
        schoolYear.setModified(LocalDateTime.now());
        schoolYear.getExams().add(exam);
        for (Student student : schoolYear.getStudents()) {
            if (!student.getExams().contains(exam)) {
                student.getExams().add(exam);
                userService.updateModel(student);
            }
        }
        schoolYearRepository.save(schoolYear);
        return mapper.map(exam, ExamExposeDTO.class);
    }

    @Override
    public HomeworkDTO addHomework(Long schoolYearId, Long homeworkId) throws NonexistingEntityException, InvalidOperationException {
        SchoolYear schoolYear = getSchoolYearByIdModel(schoolYearId);
        Homework homework = homeworkService.getHomeworkByIdModel(homeworkId);
        if (schoolYear.getHomework().contains(homework)) {
            throw new InvalidOperationException(String.format(CommonMessages.HOMEWORK_ALREADY_ASSOCIATED_WITH_THIS_SCHOOLYEAR, homework.getId(), schoolYear.getId()));
        }
        homework.setModified(LocalDateTime.now());
        schoolYear.setModified(LocalDateTime.now());
        schoolYear.getHomework().add(homework);
        for (Student student : schoolYear.getStudents()) {
            if (!student.getHomework().contains(homework)) {
                student.getHomework().add(homework);
                userService.updateModel(student);
            }
        }
        schoolYearRepository.save(schoolYear);
        return mapper.map(homework, HomeworkDTO.class);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_HEADMASTER')")
    public StudentExposeDTO removeStudent(Long schoolYearId, Long studentId) throws NonexistingEntityException {
        SchoolYear schoolYear = getSchoolYearByIdModel(schoolYearId);
        User student = userService.getUserByIdModel(studentId);
        if (!(student instanceof Student)) {
            throw new InvalidEntityDataException(String.format(CommonMessages.INVALID_USER_TYPE, student.getId(), Student.class.getSimpleName()));
        }
        if (((Student) student).getSchoolYear() != null && !Objects.equals(((Student) student).getSchoolYear().getId(), schoolYear.getId())) {
            throw new NonexistingEntityException(String.format(CommonMessages.USER_NOT_ASSOCIATED_WITH_THIS_SCHOOLYEAR, student.getId(), schoolYear.getId()));
        }
        schoolYear.getStudents().removeIf(model -> model.getId().equals(student.getId()));
        ((Student) student).setSchoolYear(null);
        ((Student) student).setGrades(new HashMap<>());
        ((Student) student).setHomework(new ArrayList<>());
        ((Student) student).setSubjects(new ArrayList<>());
        ((Student) student).setExams(new ArrayList<>());
        ((Student) student).setCompetitions(new ArrayList<>());
        ((Student) student).setTimetable(null);
        userService.updateModel(student);
        schoolYearRepository.save(schoolYear);
        return StudentExposeDTO.parseModel((Student) student);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_HEADMASTER')")
    public TeacherExposeDTO removeTeacher(Long schoolYearId, Long teacherId) throws NonexistingEntityException {
        SchoolYear schoolYear = getSchoolYearByIdModel(schoolYearId);
        User teacher = userService.getUserByIdModel(teacherId);
        if (!(teacher instanceof Teacher)) {
            throw new InvalidEntityDataException(String.format(CommonMessages.INVALID_USER_TYPE, teacher.getId(), "Teacher"));
        }
        if (!((Teacher) teacher).getSchoolYears().contains(schoolYear)) {
            throw new NonexistingEntityException(String.format(CommonMessages.USER_NOT_ASSOCIATED_WITH_THIS_SCHOOLYEAR, teacher.getId(), schoolYear.getId()));
        }
        schoolYear.getTeachers().remove(teacher);
        ((Teacher) teacher).getSchoolYears().remove(schoolYear);
        userService.updateModel(teacher);
        schoolYearRepository.save(schoolYear);
        return TeacherExposeDTO.parseModel(teacher);
    }

    @Override
    public Subject removeSubject(Long schoolYearId, Subject subject) throws NonexistingEntityException {
        SchoolYear schoolYear = getSchoolYearByIdModel(schoolYearId);
        schoolYear.getSubjects().remove(subject);
        schoolYear.getStudents().forEach(student -> student.getSubjects().remove(subject));
        for (Student student : schoolYear.getStudents()) {
            userService.updateModel(student);
        }
        schoolYearRepository.save(schoolYear);
        return subject;
    }

    @Override
    public ExamExposeDTO removeExam(Long schoolYearId, Long examId) throws NonexistingEntityException {
        SchoolYear schoolYear = getSchoolYearByIdModel(schoolYearId);
        Exam exam = examService.getExamByIdModel(examId);
        schoolYear.getExams().remove(exam);
        schoolYear.getStudents().forEach(student -> student.getExams().remove(exam));
        for (Student student : schoolYear.getStudents()) {
            userService.updateModel(student);
        }
        schoolYearRepository.save(schoolYear);
        return mapper.map(exam, ExamExposeDTO.class);
    }

    @Override
    public HomeworkDTO removeHomework(Long schoolYearId, Long homeworkId) throws NonexistingEntityException {
        SchoolYear schoolYear = getSchoolYearByIdModel(schoolYearId);
        Homework homework = homeworkService.getHomeworkByIdModel(homeworkId);
        schoolYear.getHomework().remove(homework);
        schoolYear.getStudents().forEach(student -> student.getHomework().remove(homework));
        for (Student student : schoolYear.getStudents()) {
            userService.updateModel(student);
        }
        schoolYearRepository.save(schoolYear);
        return mapper.map(homework, HomeworkDTO.class);
    }


    @Override
    @PreAuthorize("hasRole('ROLE_HEADMASTER')")
    public SchoolYearExposeDTO create() throws InvalidOperationException {
        if (Integer.parseInt(String.valueOf(schoolYearRepository.count())) >= 12) {
            throw new InvalidOperationException(CommonMessages.CANT_HAVE_MORE_THAN_TWELVE_SCHOOLYEARS);
        }
        return SchoolYearExposeDTO.parseModel(schoolYearRepository.save(new SchoolYear()));
    }

    @Override
    public TimetableDTO updateSchoolYearTimetable(Long id, UpdateTimetableDTO timetableDTO) throws NonexistingEntityException {
        SchoolYear schoolYear = getSchoolYearByIdModel(id);
        Timetable updatedTimetable = timetableService.updateModel(id, timetableDTO);
        schoolYear.setTimetable(updatedTimetable);
        schoolYear.getStudents().forEach(student -> student.setTimetable(updatedTimetable));
        for (Student student : schoolYear.getStudents()) {
            userService.updateModel(student);
        }
        schoolYearRepository.save(schoolYear);
        return mapper.map(updatedTimetable, TimetableDTO.class);
    }

    @Override
    public TimetableDTO createSchoolYearTimetable(Long id, Long timetableId) throws NonexistingEntityException, InvalidOperationException {
        SchoolYear schoolYear = getSchoolYearByIdModel(id);
        if (schoolYear.getTimetable() != null) {
            throw new InvalidOperationException(String.format(CommonMessages.SCHOOLYEAR_POSSESS_TIMETABLE, schoolYear.getId(), schoolYear.getTimetable().getId()));
        }
        Timetable timetable = timetableService.getTimetableByIdModel(timetableId);
        schoolYear.setTimetable(timetable);
        schoolYear.getStudents().forEach(student -> student.setTimetable(timetable));
        timetable.setModified(LocalDateTime.now());
        schoolYear.setModified(LocalDateTime.now());
        for (Student student : schoolYear.getStudents()) {
            userService.updateModel(student);
        }
        schoolYearRepository.save(schoolYear);
        return mapper.map(timetable, TimetableDTO.class);
    }

    @Override
    public TimetableDTO deleteSchoolYearTimetable(Long id) throws NonexistingEntityException {
        SchoolYear schoolYear = getSchoolYearByIdModel(id);
        if (schoolYear.getTimetable() == null) {
            throw new NonexistingEntityException(String.format(CommonMessages.SCHOOLYEAR_DOES_NOT_POSSESS_TIMETABLE, schoolYear.getId()));
        }
        Timetable timetable = schoolYear.getTimetable();
        timetable.setModified(LocalDateTime.now());
        schoolYear.setTimetable(null);
        schoolYear.getStudents().forEach(student -> student.setTimetable(null));
        schoolYear.setModified(LocalDateTime.now());
        for (Student student : schoolYear.getStudents()) {
            userService.updateModel(student);
        }
        schoolYearRepository.save(schoolYear);
        return mapper.map(timetable, TimetableDTO.class);
    }


    @Override
    @PreAuthorize("hasRole('ROLE_HEADMASTER')")
    public SchoolYearDeleteDTO deleteById(Long id) throws NonexistingEntityException {
        Optional<SchoolYear> schoolYearOld = schoolYearRepository.findAll().stream().filter(scy -> scy.getId().equals(id)).findFirst();
        if (schoolYearOld.isEmpty()) {
            throw new NonexistingEntityException(String.format(CommonMessages.SCHOOLYEAR_DOES_NOT_EXISTS, schoolYearOld.get().getId()));
        }
        schoolYearRepository.cleanSchoolYearExamsRelation(schoolYearOld.get().getId());
        schoolYearRepository.cleanSchoolYearHomeworkRelation(schoolYearOld.get().getId());
        schoolYearRepository.cleanSchoolYearStudentRelation(schoolYearOld.get().getId());
        schoolYearRepository.cleanSchoolYearTeacherRelation(schoolYearOld.get().getId());
        schoolYearRepository.cleanSchoolYearSubjectRelation(schoolYearOld.get().getId());
        schoolYearRepository.cleanSchoolYearUserRelation(schoolYearOld.get().getId());
        schoolYearRepository.delete(schoolYearOld.get());
        return new SchoolYearDeleteDTO(String.valueOf(id));
    }

    @Override
    @Transactional(readOnly = true)
    public String schoolYearsCount() {
        return String.valueOf(schoolYearRepository.count());
    }
}
