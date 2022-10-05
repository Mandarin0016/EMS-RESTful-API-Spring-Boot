package org.modis.EmsApplication.model;

import lombok.*;
import org.modis.EmsApplication.model.enums.Subject;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Student extends User {
    @ManyToOne
    private SchoolYear schoolYear;
    @NotNull
    @ElementCollection
    @JoinTable(name = "student_parents", joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "parent_id")
    private List<Long> parentsId = new ArrayList<>();
    @NotNull
    @OneToMany
    @JoinTable(name = "student_certificates")
    private List<Certificate> certificates = new ArrayList<>();
    @NotNull
    @ElementCollection(targetClass = Subject.class)
    @CollectionTable(name = "student_subject", joinColumns = @JoinColumn(name = "student_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "subject_name")
    private List<Subject> subjects = new ArrayList<>();
    @NotNull
    @ElementCollection
    @CollectionTable(name = "student_grades", joinColumns = @JoinColumn(name = "student_id"))
    @MapKeyColumn(name = "subject_name")
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyClass(Subject.class)
    @Column(name = "grades")
    private Map<Subject, String> grades = new HashMap<>();
    @NotNull
    @ManyToMany
    @JoinTable(name = "student_homework")
    private List<Homework> homework = new ArrayList<>();
    @NotNull
    @ManyToMany
    @JoinTable(name = "student_competitions")
    private List<Competition> competitions = new ArrayList<>();
    @NotNull
    @ManyToMany
    @JoinTable(name = "student_exams")
    private List<Exam> exams = new ArrayList<>();
    @OneToOne
    private Timetable timetable;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Student{");
        sb.append("id=").append(getId());
        sb.append('}');
        return sb.toString();
    }
}
