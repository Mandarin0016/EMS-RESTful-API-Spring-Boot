package org.modis.EmsApplication.model;

import lombok.*;
import org.modis.EmsApplication.model.enums.Subject;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity(name = "Teacher")
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Teacher extends User {
    @NonNull
    @NotNull
    @Enumerated(EnumType.STRING)
    private Subject subject;
    @NonNull
    @ManyToMany(mappedBy = "teachers")
    private Set<SchoolYear> schoolYears = new HashSet<>();
    @OneToOne
    private Timetable timetable;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Teacher{");
        sb.append("id=").append(getId());
        sb.append('}');
        return sb.toString();
    }
}
