package org.modis.EmsApplication.web;

import org.modis.EmsApplication.dto.HomeworkDTO;
import org.modis.EmsApplication.dto.StudentExposeDTO;
import org.modis.EmsApplication.service.HomeworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static org.modis.EmsApplication.utils.ErrorHandlingUtils.handleValidationErrors;

@RestController
@RequestMapping("/api/homework")
public class HomeworkRestController {

    private final HomeworkService homeworkService;

    @Autowired
    public HomeworkRestController(HomeworkService homeworkService) {
        this.homeworkService = homeworkService;
    }

    @GetMapping
    public Collection<HomeworkDTO> getAllHomework() {
        return homeworkService.getAllHomework();
    }

    @GetMapping("/{id:\\d+}")
    public HomeworkDTO getHomeworkById(@PathVariable("id") Long id) {
        return homeworkService.getHomeworkById(id);
    }

    @PostMapping
    public ResponseEntity<HomeworkDTO> createHomework(@Valid @RequestBody HomeworkDTO homeworkDTO, Errors errors) {
        handleValidationErrors(errors);
        HomeworkDTO homework = homeworkService.create(homeworkDTO);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().pathSegment("{id}").buildAndExpand(homework.getId()).toUri()).body(homework);
    }

    @PutMapping("/{id:\\d+}")
    public HomeworkDTO updateHomework(@PathVariable("id") Long id, @Valid @RequestBody HomeworkDTO homeworkDTO, Errors errors) {
        handleValidationErrors(errors);
        return homeworkService.update(id, homeworkDTO);
    }

    @DeleteMapping("/{id:\\d+}")
    public HomeworkDTO deleteHomework(@PathVariable("id") Long id) {
        return homeworkService.deleteById(id);
    }

    @GetMapping("/{id:\\d+}/results")
    public Map<StudentExposeDTO, String> getALlHomeworkResults(@PathVariable("id") Long homeworkID) {
        return homeworkService.getAllHomeworkPerformers(homeworkID);
    }

    @PostMapping("/{id:\\d+}/results")
    public HomeworkDTO submitHomeworkResult(@PathVariable("id") Long homeworkID, @RequestParam(name = "studentId") Long studentId, @RequestBody String studentAnswer) {
        return homeworkService.submitHomework(homeworkID, studentId, studentAnswer);
    }

    @PostMapping("/{id:\\d+}/results/{studentId:\\d+}")
    public StudentExposeDTO gradeHomeworkResult(@PathVariable("id") Long homeworkID, @PathVariable("studentId") Long studentId, @RequestParam(name = "grade") String grade) {
        return homeworkService.gradeHomework(homeworkID, studentId, grade);
    }


    @GetMapping("/count")
    public String getHomeworkCount() {
        return homeworkService.homeworkCount();
    }

}
