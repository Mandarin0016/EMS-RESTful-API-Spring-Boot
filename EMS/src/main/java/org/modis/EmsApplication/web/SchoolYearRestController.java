package org.modis.EmsApplication.web;

import org.modis.EmsApplication.dto.*;
import org.modis.EmsApplication.exception.InvalidOperationException;
import org.modis.EmsApplication.model.*;
import org.modis.EmsApplication.model.enums.Subject;
import org.modis.EmsApplication.service.SchoolYearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping("/api/schoolyears")
public class SchoolYearRestController {

    private final SchoolYearService schoolYearService;

    @Autowired
    public SchoolYearRestController(SchoolYearService schoolYearService) {
        this.schoolYearService = schoolYearService;
    }

    @GetMapping
    public Collection<SchoolYearExposeDTO> getAllSchoolYears() {
        return schoolYearService.getAllSchoolYears();
    }

    @GetMapping("/{id:\\d+}")
    public SchoolYearExposeDTO getSchoolYearById(@PathVariable("id") Long id) {
        return schoolYearService.getSchoolYearById(id);
    }

    @PostMapping
    public ResponseEntity<SchoolYearExposeDTO> createSchoolYear() throws InvalidOperationException {
        SchoolYearExposeDTO schoolYear = schoolYearService.create();
        return ResponseEntity
                .created(ServletUriComponentsBuilder.fromCurrentRequest().pathSegment("{id}").buildAndExpand(schoolYear.getId()).toUri())
                .body(schoolYear);
    }

    @DeleteMapping("/{id:\\d+}")
    public SchoolYearDeleteDTO deleteSchoolYearById(@PathVariable("id") Long id) {
        return schoolYearService.deleteById(id);
    }

    @GetMapping("/{id:\\d+}/subjects")
    public Collection<Subject> getAllSchoolYearSubjects(@PathVariable("id") Long id) {
        return schoolYearService.getSchoolYearSubjects(id);
    }

    @PostMapping("/{id:\\d+}/subjects")
    public Subject addSchoolYearSubject(@PathVariable("id") Long id, @RequestParam(name = "subject") String subjectName) throws InvalidOperationException {
        return schoolYearService.addSubject(id, Subject.valueOf(subjectName));
    }

    @DeleteMapping("/{id:\\d+}/subjects")
    public Subject removeSchoolYearSubject(@PathVariable("id") Long id, @RequestParam(name = "subject") String subjectName) {
        return schoolYearService.removeSubject(id, Subject.valueOf(subjectName));
    }

    @GetMapping("/{id:\\d+}/students")
    public Collection<StudentExposeDTO> getAllSchoolYearStudents(@PathVariable("id") Long id) {
        return schoolYearService.getSchoolYearStudents(id);
    }

    @PostMapping("/{id:\\d+}/students")
    public StudentExposeDTO addSchoolYearStudent(@PathVariable("id") Long id, @RequestParam(name = "id") Long studentId) throws InvalidOperationException {
        return schoolYearService.addStudent(id, studentId);
    }

    @DeleteMapping("/{id:\\d+}/students")
    public StudentExposeDTO removeSchoolYearStudent(@PathVariable("id") Long id, @RequestParam(name = "id") Long studentId) throws InvalidOperationException {
        return schoolYearService.removeStudent(id, studentId);
    }

    @GetMapping("/{id:\\d+}/teachers")
    public Collection<TeacherExposeDTO> getAllSchoolYearTeachers(@PathVariable("id") Long id) {
        return schoolYearService.getSchoolYearTeachers(id);
    }

    @PostMapping("/{id:\\d+}/teachers")
    public TeacherExposeDTO addSchoolYearTeacher(@PathVariable("id") Long id, @RequestParam(name = "id") Long teacherId) throws InvalidOperationException {
        return schoolYearService.addTeacher(id, teacherId);
    }

    @DeleteMapping("/{id:\\d+}/teachers")
    public TeacherExposeDTO removeSchoolYearTeacher(@PathVariable("id") Long id, @RequestParam(name = "id") Long teacherId) throws InvalidOperationException {
        return schoolYearService.removeTeacher(id, teacherId);
    }

    @GetMapping("/{id:\\d+}/exams")
    public Collection<ExamExposeDTO> getAllSchoolYearExams(@PathVariable("id") Long id) {
        return schoolYearService.getSchoolYearExams(id);
    }

    @PostMapping("/{id:\\d+}/exams")
    public ExamExposeDTO addSchoolYearExam(@PathVariable("id") Long id, @RequestParam(name = "id") Long examId) throws InvalidOperationException {
        return schoolYearService.addExam(id, examId);
    }

    @DeleteMapping("/{id:\\d+}/exams")
    public ExamExposeDTO removeSchoolYearExam(@PathVariable("id") Long id, @RequestParam(name = "id") Long examId) {
        return schoolYearService.removeExam(id, examId);
    }

    @GetMapping("/{id:\\d+}/homework")
    public Collection<HomeworkDTO> getAllSchoolYearHomework(@PathVariable("id") Long id) {
        return schoolYearService.getSchoolYearHomework(id);
    }

    @PostMapping("/{id:\\d+}/homework")
    public HomeworkDTO addSchoolYearHomework(@PathVariable("id") Long id, @RequestParam(name = "id") Long homeworkId) throws InvalidOperationException {
        return schoolYearService.addHomework(id, homeworkId);
    }

    @DeleteMapping("/{id:\\d+}/homework")
    public HomeworkDTO removeSchoolYearHomework(@PathVariable("id") Long id, @RequestParam(name = "id") Long homeworkId) throws InvalidOperationException {
        return schoolYearService.removeHomework(id, homeworkId);
    }

    @GetMapping("/{id:\\d+}/timetable")
    public TimetableDTO getSchoolYearTimetable(@PathVariable("id") Long id) {
        return schoolYearService.getSchoolYearTimetable(id);
    }

    @PostMapping("/{id:\\d+}/timetable")
    public TimetableDTO createSchoolYearTimetable(@PathVariable("id") Long id, @RequestParam(name = "timetableId") Long timetableId) throws InvalidOperationException {
        return schoolYearService.createSchoolYearTimetable(id, timetableId);
    }

    @PutMapping("/{id:\\d+}/timetable")
    public TimetableDTO updateSchoolYearTimetable(@PathVariable("id") Long id, @Valid @RequestBody UpdateTimetableDTO timetableDTO) {
        return schoolYearService.updateSchoolYearTimetable(id, timetableDTO);
    }

    @DeleteMapping("/{id:\\d+}/timetable")
    public TimetableDTO deleteSchoolYearTimetable(@PathVariable("id") Long id) throws InvalidOperationException {
        return schoolYearService.deleteSchoolYearTimetable(id);
    }

    @GetMapping("/count")
    public String getSchoolYearCount() {
        return schoolYearService.schoolYearsCount();
    }
}
