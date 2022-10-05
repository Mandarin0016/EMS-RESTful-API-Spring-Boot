package org.modis.EmsApplication.dto;

import lombok.Data;
import org.modelmapper.ModelMapper;
import org.modis.EmsApplication.model.*;
import org.modis.EmsApplication.model.enums.Subject;

import java.util.List;
import java.util.Set;

@Data

public class SchoolYearExposeDTO {

    private Long id;
    private List<StudentExposeDTO> students;
    private List<TeacherExposeDTO> teachers;
    private Set<Subject> subjects;
    private List<ExamExposeDTO> exams;
    private List<HomeworkDTO> homework;
    private TimetableDTO timetable;

    public static SchoolYearExposeDTO parseModel(SchoolYear model) {
        ModelMapper mapper = new ModelMapper();
        SchoolYearExposeDTO schoolYearExposeDTO = new SchoolYearExposeDTO();
        schoolYearExposeDTO.setId(model.getId());
        schoolYearExposeDTO.setStudents(model.getStudents().stream().map(StudentExposeDTO::parseModel).toList());
        schoolYearExposeDTO.setTeachers(model.getTeachers().stream().map(TeacherExposeDTO::parseModel).toList());
        schoolYearExposeDTO.setSubjects(model.getSubjects());
        schoolYearExposeDTO.setExams(model.getExams().stream().map(examModel -> mapper.map(examModel, ExamExposeDTO.class)).toList());
        schoolYearExposeDTO.setHomework(model.getHomework().stream().map(homeworkModel -> mapper.map(homeworkModel, HomeworkDTO.class)).toList());
        if (model.getTimetable() == null) {
            schoolYearExposeDTO.setTimetable(null);
        } else {
            schoolYearExposeDTO.setTimetable(mapper.map(model.getTimetable(), TimetableDTO.class));
        }
        return schoolYearExposeDTO;
    }

}
