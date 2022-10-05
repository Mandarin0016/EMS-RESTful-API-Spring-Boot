package org.modis.EmsApplication.dto;

import lombok.Data;
import org.modelmapper.ModelMapper;
import org.modis.EmsApplication.model.*;
import org.modis.EmsApplication.model.enums.AccountStatus;
import org.modis.EmsApplication.model.enums.Gender;
import org.modis.EmsApplication.model.enums.Role;
import org.modis.EmsApplication.model.enums.Subject;

import java.time.LocalDateTime;
import java.util.*;

@Data
public class StudentExposeDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Gender gender;
    private Role role;
    private AccountStatus accountStatus;
    private LocalDateTime created;
    private LocalDateTime modified;
    private Long schoolYear;
    private List<Long> parentsId;
    private List<CertificateExposeDTO> certificates;
    private List<Subject> subjects;
    private Map<Subject, String> grades;
    private List<HomeworkDTO> homework;
    private List<String> competitions;
    private List<String> exams;
    private Timetable timetable;

    public StudentExposeDTO(User model) {
        this.setId(model.getId());
        this.setFirstName(model.getFirstName());
        this.setLastName(model.getLastName());
        this.setEmail(model.getEmail());
        this.setGender(model.getGender());
        this.setRole(model.getRole());
        this.setAccountStatus(model.getAccountStatus());
        this.setCreated(model.getCreated());
        this.setModified(model.getModified());
    }


    public static StudentExposeDTO parseModel(Student model) {
        ModelMapper mapper = new ModelMapper();
        StudentExposeDTO studentExposeDTO = new StudentExposeDTO(model);
        if (model.getSchoolYear() != null) {
            studentExposeDTO.setSchoolYear(model.getSchoolYear().getId());
        }
        studentExposeDTO.setParentsId(model.getParentsId());
        studentExposeDTO.setCertificates(model.getCertificates().stream().map(certificate -> mapper.map(certificate, CertificateExposeDTO.class)).toList());
        studentExposeDTO.setSubjects(model.getSubjects());
        studentExposeDTO.setGrades(model.getGrades());
        studentExposeDTO.setHomework(model.getHomework().stream().map(homework -> mapper.map(homework, HomeworkDTO.class)).toList());
        studentExposeDTO.setCompetitions(new ArrayList<>());
        for (Competition competition : model.getCompetitions()) {
            String competitionForExposure = "Competition with ID = '" + competition.getId() + "'.";
            studentExposeDTO.getCompetitions().add(competitionForExposure);
        }
        studentExposeDTO.setExams(new ArrayList<>());
        for (Exam exam : model.getExams()) {
            studentExposeDTO.getExams().add(String.format("Exam with ID='%s'", exam.getId()));
        }
        studentExposeDTO.setTimetable(model.getTimetable());
        return studentExposeDTO;
    }
}
