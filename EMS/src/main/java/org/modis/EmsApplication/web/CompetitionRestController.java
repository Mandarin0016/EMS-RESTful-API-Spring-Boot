package org.modis.EmsApplication.web;

import org.modis.EmsApplication.dto.*;
import org.modis.EmsApplication.exception.InvalidOperationException;
import org.modis.EmsApplication.service.CompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.modis.EmsApplication.utils.ErrorHandlingUtils.handleValidationErrors;

@RestController
@RequestMapping("/api/competitions")
public class CompetitionRestController {

    private final CompetitionService competitionService;

    @Autowired
    public CompetitionRestController(CompetitionService competitionService) {
        this.competitionService = competitionService;
    }

    @GetMapping
    public Collection<CompetitionExposeDTO> getAllCompetitions() {
        return competitionService.getAllCompetitions();
    }

    @GetMapping("/{id:\\d+}")
    public CompetitionExposeDTO getCompetitionById(@PathVariable("id") Long id) {
        return competitionService.getCompetitionById(id);
    }

    @PostMapping
    public ResponseEntity<CompetitionExposeDTO> createCompetition(@Valid @RequestBody CompetitionDTO competitionDTO, Errors errors) {
        handleValidationErrors(errors);
        CompetitionExposeDTO createdCompetition = competitionService.create(competitionDTO);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().pathSegment("{id}").buildAndExpand(createdCompetition.getId()).toUri()).body(createdCompetition);
    }

    @PutMapping("/{id:\\d+}")
    public CompetitionExposeDTO updateCompetition(@PathVariable("id") Long id, @Valid @RequestBody CompetitionDTO competitionDTO, Errors errors) {
        handleValidationErrors(errors);
        return competitionService.update(id, competitionDTO);
    }

    @DeleteMapping("/{id:\\d+}")
    public CompetitionExposeDTO deleteCompetitionById(@PathVariable("id") Long id) {
        return competitionService.deleteById(id);
    }

    @PostMapping("/{id:\\d+}/performers")
    public CompetitionExposeDTO performCompetition(@PathVariable("id") Long id, @RequestParam(name = "studentId") Long studentId, @Valid @RequestBody List<Integer> studentAnswer) throws InvalidOperationException {
        return competitionService.performCompetition(id, studentId, studentAnswer);
    }

    @GetMapping("/{id:\\d+}/certificate")
    public CertificateExposeDTO getCompetitionCertificate(@PathVariable("id") Long id) throws InvalidOperationException {
        return competitionService.getCertificate(id);
    }

    @PutMapping("/{id:\\d+}/certificate")
    public CompetitionExposeDTO assignCompetitionCertificate(@PathVariable("id") Long id, @RequestParam("certificateId") Long certificateId) throws InvalidOperationException {
        return competitionService.assignCertificateById(id, certificateId);
    }

    @DeleteMapping("/{id:\\d+}/certificate")
    public CompetitionExposeDTO deleteCompetitionCertificate(@PathVariable("id") Long id) throws InvalidOperationException {
        return competitionService.deleteCertificate(id);
    }

    @GetMapping("/{id:\\d+}/registered")
    public List<StudentExposeDTO> getCompetitionRegisteredStudents(@PathVariable("id") Long id) throws InvalidOperationException {
        return competitionService.getRegisteredStudents(id);
    }

    @GetMapping("/{id:\\d+}/results")
    public Map<StudentExposeDTO, Double> getCompetitionResults(@PathVariable("id") Long id) {
        return competitionService.showCompetitionResults(id);
    }

    @GetMapping("/{id:\\d+}/winner")
    public UserExposeDTO getCompetitionWinner(@PathVariable("id") Long id) throws InvalidOperationException {
        return competitionService.getCompetitionWinner(id);
    }

    @PutMapping("/{id:\\d+}/winner")
    public CompetitionExposeDTO updateCompetitionWinner(@PathVariable("id") Long id, @RequestParam(name = "studentId") Long studentId) throws InvalidOperationException {
        return competitionService.changeCompetitionWinner(id, studentId);
    }

    @DeleteMapping("/{id:\\d+}/winner")
    public CompetitionExposeDTO deleteCompetitionWinner(@PathVariable("id") Long id) throws InvalidOperationException {
        return competitionService.deleteCompetitionWinner(id);
    }

    @GetMapping("/count")
    public String getCompetitionsCount() {
        return competitionService.competitionsCount();
    }

}
