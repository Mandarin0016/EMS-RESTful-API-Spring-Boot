package org.modis.EmsApplication.web;

import org.modis.EmsApplication.dto.TimetableDTO;
import org.modis.EmsApplication.dto.UpdateTimetableDTO;
import org.modis.EmsApplication.exception.InvalidEntityDataException;
import org.modis.EmsApplication.exception.NonexistingEntityException;
import org.modis.EmsApplication.model.Timetable;
import org.modis.EmsApplication.service.TimetableService;
import org.modis.EmsApplication.utils.CommonMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.Collection;

import static org.modis.EmsApplication.utils.ErrorHandlingUtils.handleValidationErrors;

@RestController
@RequestMapping("/api/timetables")
public class TimetableRestController {

    private final TimetableService timetableService;

    @Autowired
    public TimetableRestController(TimetableService timetableService) {
        this.timetableService = timetableService;
    }

    @GetMapping
    public Collection<TimetableDTO> getAllTimetables() {
        return timetableService.getAllTimetables();
    }

    @GetMapping("/{id:\\d+}")
    public TimetableDTO getTimetableById(@PathVariable("id") Long id) throws NonexistingEntityException {
        return timetableService.getTimetableById(id);
    }

    @PostMapping
    public ResponseEntity<TimetableDTO> createTimetable(@Valid @RequestBody TimetableDTO timetableDTO, Errors errors) {
        handleValidationErrors(errors);
        TimetableDTO timetable = timetableService.create(timetableDTO);

        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().pathSegment("{id}").buildAndExpand(timetable.getId()).toUri()).body(timetable);
    }

    @PutMapping("/{id:\\d+}")
    public TimetableDTO updateTimetable(@PathVariable("id") Long id, @Valid @RequestBody UpdateTimetableDTO timetableDTO, Errors errors) {
        handleValidationErrors(errors);
        if (!id.equals(timetableDTO.getId())) {
            throw new InvalidEntityDataException(String.format(CommonMessages.DIFFERENT_ID_IN_REQUEST_BODY_AND_URL, id, timetableDTO.getId()));
        }
        return timetableService.update(id, timetableDTO);
    }

    @DeleteMapping("{id:\\d+}")
    public TimetableDTO deleteTimetableById(@PathVariable("id") Long id) {
        return timetableService.deleteById(id);
    }

    @GetMapping("/count")
    public String getTimetablesCount() {
        return timetableService.count();
    }


}
