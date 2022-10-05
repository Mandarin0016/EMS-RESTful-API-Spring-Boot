package org.modis.EmsApplication.web;

import org.modis.EmsApplication.dto.*;
import org.modis.EmsApplication.exception.InvalidEntityDataException;
import org.modis.EmsApplication.exception.InvalidOperationException;
import org.modis.EmsApplication.model.Exam;
import org.modis.EmsApplication.service.ExamService;
import org.modis.EmsApplication.utils.CommonMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;

import static org.modis.EmsApplication.utils.ErrorHandlingUtils.handleValidationErrors;

@RestController
@RequestMapping("/api/exams")
public class ExamRestController {

    private final ExamService examService;

    @Autowired
    public ExamRestController(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping
    public Collection<ExamExposeDTO> getAllExams() {
        return this.examService.getAllExams();
    }

    @GetMapping("/{id:\\d+}")
    public ExamExposeDTO getExamById(@PathVariable("id") Long id) {
        return examService.getExamById(id);
    }

    @PostMapping
    public ResponseEntity<ExamExposeDTO> createExam(@Valid @RequestBody CreateExamDTO examDTO, Errors errors) {
        handleValidationErrors(errors);
        ExamExposeDTO createdExam = examService.create(examDTO);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().pathSegment("{id}")
                .buildAndExpand(createdExam.getId()).toUri()).body(createdExam);
    }

    @PutMapping("/{id:\\d+}")
    public ExamExposeDTO updateExam(@PathVariable("id") Long id, @Valid @RequestBody UpdateExamDTO examDTO, Errors errors) {
        handleValidationErrors(errors);
        if (!id.equals(examDTO.getId())) {
            throw new InvalidEntityDataException(String.format(CommonMessages.DIFFERENT_ID_IN_REQUEST_BODY_AND_URL, id, examDTO.getId()));
        }
        return examService.update(id, examDTO);
    }

    @DeleteMapping("/{id:\\d+}")
    public ExamExposeDTO deleteExamById(@PathVariable("id") Long id) {
        return examService.deleteById(id);
    }

    @GetMapping("/{id:\\d+}/performers")
    public Map<UserExposeDTO, String> getAllExamPerformers(@PathVariable("id") Long id) {
        return examService.getAllPerformers(id);
    }

    @PostMapping("/{id:\\d+}/performers")
    public ExamExposeDTO createExamPerformer(@PathVariable("id") Long id, @RequestParam(name = "studentId") Long studentId, @RequestBody String studentAnswers) throws InvalidOperationException {
        return examService.performExam(studentId, id, studentAnswers);
    }

    @GetMapping("/{id:\\d+}/performers/{studentId:\\d+}")
    public AbstractMap.SimpleEntry<Long, String> getExamPerformerById(@PathVariable("id") Long id, @PathVariable("studentId") Long studentId) {
        return examService.getPerformerById(id, studentId);
    }

    @GetMapping("/{id:\\d+}/results")
    public Map<StudentExposeDTO, Double> getAllExamResults(@PathVariable("id") Long id) throws InvalidOperationException {
        return examService.getAllExamResults(id);
    }

    @PostMapping("/{id:\\d+}/results")
    public ExamExposeDTO createExamResult(@PathVariable("id") Long id, @RequestParam(name = "studentId") Long studentId, @RequestParam(name = "grade") String grade) throws InvalidOperationException {
        return examService.gradeExamResult(studentId, id, grade);
    }

    @GetMapping("/{id:\\d+}/results/{studentId:\\d+}")
    public Map.Entry<UserExposeDTO, Double> getExamResultById(@PathVariable("id") Long id, @PathVariable("studentId") Long studentId) throws InvalidOperationException {
        return examService.getStudentExamResults(id, studentId);
    }

    @PutMapping("/{id:\\d+}/results/{studentId:\\d+}")
    public Map<StudentExposeDTO, Double> updateExamResult(@PathVariable("id") Long id, @PathVariable("studentId") Long studentId, @RequestParam(name = "grade") String grade) throws InvalidOperationException {
        return examService.updateExamResult(id, studentId, grade);
    }

    @DeleteMapping("/{id:\\d+}/results/{studentId:\\d+}")
    public Map<StudentExposeDTO, Double> deleteExamResult(@PathVariable("id") Long id, @PathVariable("studentId") Long studentId) throws InvalidOperationException {
        return examService.removeExamResult(id, studentId);
    }

    @GetMapping("/count")
    public String getExamCount() {
        return examService.examsCount();
    }

}
