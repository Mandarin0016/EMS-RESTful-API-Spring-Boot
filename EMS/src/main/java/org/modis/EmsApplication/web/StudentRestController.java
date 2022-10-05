package org.modis.EmsApplication.web;

import org.modis.EmsApplication.dto.*;
import org.modis.EmsApplication.exception.InvalidOperationException;
import org.modis.EmsApplication.model.*;
import org.modis.EmsApplication.model.enums.Subject;
import org.modis.EmsApplication.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.modis.EmsApplication.utils.ErrorHandlingUtils.handleValidationErrors;

@RestController
@RequestMapping("/api/students")
public class StudentRestController {

    private final StudentService studentService;

    @Autowired
    public StudentRestController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public Collection<StudentExposeDTO> getAllStudents() {
        return this.studentService.getAllStudents();
    }

    @GetMapping("/{id:\\d+}")
    public StudentExposeDTO getStudentById(@PathVariable("id") Long id) {
        return studentService.getStudentById(id);
    }

    @GetMapping("/{id}/grades")
    @ResponseStatus(HttpStatus.OK)
    public Map<Subject, List<BigDecimal>> getAllStudentGrades(@PathVariable("id") Long id) throws InvalidOperationException {
        return this.studentService.getAllStudentGrades(id);
    }

    @PutMapping("/{id}/grades")
    public ResponseEntity<Map<Subject, List<BigDecimal>>> createStudentGrade(@PathVariable("id") Long id, @RequestParam(name = "subject") String subject, @RequestParam(name = "grade") String grade) throws InvalidOperationException {
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().build().toUri()).body(studentService.addStudentGrade(id, subject, grade));
    }

    @DeleteMapping("/{id}/grades")
    public ResponseEntity<Map<Subject, List<BigDecimal>>> deleteStudentGrade(@PathVariable("id") Long id, @RequestParam(name = "subject") String subject, @RequestParam(name = "grade") String grade) throws InvalidOperationException {
        return ResponseEntity.ok(studentService.deleteStudentGrade(id, subject, grade));
    }

    @GetMapping("/{id}/homework")
    public List<HomeworkDTO> getAllStudentHomework(@PathVariable("id") Long id) throws InvalidOperationException {
        return studentService.getAllStudentHomework(id);
    }

    @PostMapping("/{id}/homework")
    public ResponseEntity<HomeworkDTO> createSingleHomework(@PathVariable("id") Long id, @Valid @RequestBody HomeworkDTO homeworkDTO, Errors errors) throws InvalidOperationException {
        handleValidationErrors(errors);
        HomeworkDTO createdHomework = studentService.addSingleHomework(id, homeworkDTO);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().replacePath("/api/homework/{id}").buildAndExpand(createdHomework.getId()).toUri()).body(createdHomework);
    }

    @PutMapping("/{id}/homework")
    public HomeworkDTO addHomeworkById(@PathVariable("id") Long id, @RequestParam(name = "homeworkId") Long homeworkId) throws InvalidOperationException {
        return studentService.addSingleHomeworkById(id, homeworkId);
    }

    @DeleteMapping("/{id}/homework")
    public HomeworkDTO deleteHomeworkById(@PathVariable("id") Long id, @RequestParam(name = "homeworkId") Long homeworkId) throws InvalidOperationException {
        return studentService.deleteSingleHomeworkById(id, homeworkId);
    }

    @GetMapping("/{id}/exams")
    @ResponseStatus(HttpStatus.OK)
    public List<ExamExposeDTO> getAllStudentExam(@PathVariable("id") Long id) throws InvalidOperationException {
        return studentService.getAllStudentExam(id);
    }

    @PostMapping("/{id}/exams")
    public ResponseEntity<ExamExposeDTO> createSingleExam(@PathVariable("id") Long id, @Valid @RequestBody CreateExamDTO examDTO, Errors errors) throws InvalidOperationException {
        handleValidationErrors(errors);
        ExamExposeDTO createdExam = studentService.addSingleExam(id, examDTO);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().replacePath("/api/exams/{id}").buildAndExpand(createdExam.getId()).toUri()).body(createdExam);
    }

    @PutMapping("/{id}/exams")
    public ExamExposeDTO addExamById(@PathVariable("id") Long id, @RequestParam(name = "examId") Long examId) throws InvalidOperationException {
        return studentService.addSingleExamById(id, examId);
    }

    @DeleteMapping("/{id}/exams")
    public ExamExposeDTO deleteExamById(@PathVariable("id") Long id, @RequestParam(name = "examId") Long examId) throws InvalidOperationException {
        return studentService.deleteSingleExamById(id, examId);
    }

    @GetMapping("/{id}/parents")
    @ResponseStatus(HttpStatus.OK)
    public List<ParentExposeDTO> getAllStudentParents(@PathVariable("id") Long id) throws InvalidOperationException {
        return studentService.getAllStudentParents(id);
    }

    @PostMapping("/{id}/parents")
    public StudentExposeDTO addStudentParentById(@PathVariable("id") Long id, @RequestParam(name = "parentId") Long parentId) throws InvalidOperationException {
        return studentService.addStudentParent(id, parentId);
    }

    @DeleteMapping("/{id}/parents")
    public StudentExposeDTO deleteStudentParentById(@PathVariable("id") Long id, @RequestParam(name = "parentId") Long parentId) throws InvalidOperationException {
        return studentService.deleteStudentParent(id, parentId);
    }

    @GetMapping("/{id}/competitions")
    @ResponseStatus(HttpStatus.OK)
    public List<CompetitionExposeDTO> getAllStudentCompetitions(@PathVariable("id") Long id) throws InvalidOperationException {
        return studentService.getAllStudentCompetition(id);
    }

    @PostMapping("/{id}/competitions")
    public CompetitionExposeDTO addStudentCompetitionById(@PathVariable("id") Long id, @RequestParam(name = "competitionId") Long competitionId) throws InvalidOperationException {
        return studentService.addCompetition(id, competitionId);
    }

    @DeleteMapping("/{id}/competitions")
    public CompetitionExposeDTO deleteStudentCompetitionById(@PathVariable("id") Long id, @RequestParam(name = "competitionId") Long competitionId) throws InvalidOperationException {
        return studentService.removeCompetition(id, competitionId);
    }

    @GetMapping("/count")
    @ResponseStatus(HttpStatus.OK)
    public String getStudentsCount() {
        return studentService.getStudentsCount();
    }

}
