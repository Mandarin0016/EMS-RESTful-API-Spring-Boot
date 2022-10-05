package org.modis.EmsApplication.model;


import lombok.*;
import org.modis.EmsApplication.model.enums.Subject;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Homework {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @NonNull
    private String description;
    @NonNull
    @Enumerated(EnumType.STRING)
    private Subject subject;
    @ElementCollection
    @CollectionTable(name = "HomeworkAnswers", joinColumns = @JoinColumn(name = "homework_id"))
    @MapKeyJoinColumn(name = "student_id")
    @Column(name = "answer", columnDefinition = "LONGTEXT")
    private Map<Student, String> submittedStudentAnswers = new HashMap<>();
    private LocalDateTime created = LocalDateTime.now();
    private LocalDateTime modified = LocalDateTime.now();

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Homework{");
        sb.append("id=").append(id);
        sb.append('}');
        return sb.toString();
    }
}
