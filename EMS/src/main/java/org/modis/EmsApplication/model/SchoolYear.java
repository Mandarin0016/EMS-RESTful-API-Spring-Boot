package org.modis.EmsApplication.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.modis.EmsApplication.model.enums.Subject;

import javax.persistence.*;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SchoolYear {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @ManyToMany
    private final List<Student> students = new ArrayList<>();
    @ManyToMany
    private final List<Teacher> teachers = new ArrayList<>();
    @NotNull
    @ElementCollection(targetClass = Subject.class)
    @CollectionTable(name = "schoolyear_subjects", joinColumns = @JoinColumn(name = "schoolyear_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "subject_name")
    private final Set<Subject> subjects = new HashSet<>();
    @ManyToMany
    private final List<Exam> exams = new ArrayList<>();
    @ManyToMany
    private final List<Homework> homework = new ArrayList<>();
    @OneToOne
    private Timetable timetable;
    private LocalDateTime created = LocalDateTime.now();
    private LocalDateTime modified = LocalDateTime.now();

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SchoolYear{");
        sb.append("id=").append(id);
        sb.append('}');
        return sb.toString();
    }
}
