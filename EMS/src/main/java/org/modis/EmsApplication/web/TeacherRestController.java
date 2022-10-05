package org.modis.EmsApplication.web;

import org.modis.EmsApplication.dto.TeacherExposeDTO;
import org.modis.EmsApplication.dto.TimetableDTO;
import org.modis.EmsApplication.dto.UserExposeDTO;
import org.modis.EmsApplication.exception.InvalidEntityDataException;
import org.modis.EmsApplication.exception.InvalidOperationException;
import org.modis.EmsApplication.model.Teacher;
import org.modis.EmsApplication.model.Timetable;
import org.modis.EmsApplication.model.User;
import org.modis.EmsApplication.service.TeacherService;
import org.modis.EmsApplication.utils.CommonMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teachers")
public class TeacherRestController {

    private final TeacherService teacherService;

    @Autowired
    public TeacherRestController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping
    public List<TeacherExposeDTO> getAllTeachers() {
        return teacherService.getAllTeachers();
    }

    @GetMapping("/{id:\\d+}")
    public TeacherExposeDTO getTeacherById(@PathVariable("id") Long id) {
        return teacherService.getTeacherById(id);
    }

    @GetMapping("/{teacherID}/timetable")
    public ResponseEntity<TimetableDTO> getTimetable(@PathVariable("teacherID") Long teacherId) throws InvalidOperationException {
        return ResponseEntity.ok(teacherService.getTimetable(teacherId));
    }

    @PostMapping("/{teacherID}/timetable/{id}")
    public ResponseEntity<TimetableDTO> setTimetable(@PathVariable("teacherID") Long teacherId, @PathVariable(name = "id") Long timetableId) throws InvalidOperationException {
        return ResponseEntity.ok(teacherService.setTimetable(teacherId, timetableId));
    }

    @DeleteMapping("/{teacherID}/timetable")
    public ResponseEntity<TimetableDTO> deleteTimetable(@PathVariable("teacherID") Long teacherId) throws InvalidOperationException {
        return ResponseEntity.ok(teacherService.deleteTimetable(teacherId));
    }

    @GetMapping("/count")
    public String getTeachersCount() {
        return String.valueOf(teacherService.getAllTeachers().size());
    }

}
