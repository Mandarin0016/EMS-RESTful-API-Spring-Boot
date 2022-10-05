package org.modis.EmsApplication.model;

import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.modis.EmsApplication.model.enums.Subject;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Timetable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @NonNull
    private String title;
    @NonNull
    private String contentUrl;
    private LocalDateTime created = LocalDateTime.now();
    private LocalDateTime modified = LocalDateTime.now();

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Timetable{");
        sb.append("id=").append(id);
        sb.append('}');
        return sb.toString();
    }
}
