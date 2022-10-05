package org.modis.EmsApplication.dto;

import lombok.Data;
import org.modelmapper.ModelMapper;
import org.modis.EmsApplication.model.SchoolYear;
import org.modis.EmsApplication.model.Teacher;
import org.modis.EmsApplication.model.User;
import org.modis.EmsApplication.model.enums.AccountStatus;
import org.modis.EmsApplication.model.enums.Gender;
import org.modis.EmsApplication.model.enums.Role;
import org.modis.EmsApplication.model.enums.Subject;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class TeacherExposeDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Gender gender;
    private Role role;
    private AccountStatus accountStatus;
    private LocalDateTime created;
    private LocalDateTime modified;
    private Subject subject;
    private Set<Long> schoolYears;
    private TimetableDTO timetable;
    public TeacherExposeDTO(User model) {
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

    public static TeacherExposeDTO parseModel(User model) {
        ModelMapper mapper = new ModelMapper();
        TeacherExposeDTO teacherExposeDTO = new TeacherExposeDTO(model);
        teacherExposeDTO.setSubject(((Teacher) model).getSubject());
        teacherExposeDTO.setSchoolYears(((Teacher) model).getSchoolYears().stream().map(SchoolYear::getId).collect(Collectors.toSet()));
        if (((Teacher) model).getTimetable() == null) {
            teacherExposeDTO.setTimetable(null);
        } else {
            teacherExposeDTO.setTimetable(mapper.map(((Teacher) model).getTimetable(), TimetableDTO.class));
        }
        return teacherExposeDTO;
    }
}
