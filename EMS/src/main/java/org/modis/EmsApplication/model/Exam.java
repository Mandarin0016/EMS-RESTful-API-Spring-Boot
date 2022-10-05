package org.modis.EmsApplication.model;

import lombok.*;
import org.modis.EmsApplication.model.enums.Subject;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Entity
@Data
@RequiredArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @NonNull
    @Enumerated(EnumType.STRING)
    private Subject subject;
    @NonNull
    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;
    @ElementCollection
    @CollectionTable(name = "ExamAnswers", joinColumns = @JoinColumn(name = "exam_id"))
    @MapKeyJoinColumn(name = "student_id")
    @Column(name = "answer")
    private Map<Student, String> submittedStudentAnswers = new HashMap<>();
    @ElementCollection
    @CollectionTable(name = "ExamResults", joinColumns = @JoinColumn(name = "exam_id"))
    @MapKeyJoinColumn(name = "student_id")
    @Column(name = "score")
    private Map<Student, Double> results = new HashMap<>();
    private LocalDateTime created = LocalDateTime.now();
    private LocalDateTime modified = LocalDateTime.now();

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Exam{");
        sb.append("id=").append(id);
        sb.append('}');
        return sb.toString();
    }
}
