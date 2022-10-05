package org.modis.EmsApplication.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Data
@Entity
@RequiredArgsConstructor
@NoArgsConstructor
public class CompetitionQuestion {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    private String title;
    @NonNull
    private String answers;
    @NonNull
    private int realAnswer;

    public boolean isCorrect(int answer) {
        return this.realAnswer == answer;
    }

}
