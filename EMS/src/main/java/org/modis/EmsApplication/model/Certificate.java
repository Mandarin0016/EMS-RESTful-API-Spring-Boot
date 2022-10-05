package org.modis.EmsApplication.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    private Long ownerId;
    @NonNull
    private String imageUrl;
    private LocalDateTime created = LocalDateTime.now();
    private LocalDateTime modified = LocalDateTime.now();

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Certificate{");
        sb.append("id=").append(id);
        sb.append('}');
        return sb.toString();
    }
}
