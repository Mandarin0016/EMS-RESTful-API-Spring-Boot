package org.modis.EmsApplication.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.modis.EmsApplication.exception.NonexistingEntityException;
import org.modis.EmsApplication.utils.CommonMessages;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Competition {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @OneToMany(cascade = CascadeType.ALL)
    private List<CompetitionQuestion> questions;
    private Integer COMPETITION_QUESTION_POINTS;
    @ElementCollection
    @CollectionTable(name = "CompetitionPerformers", joinColumns = @JoinColumn(name = "competition_id"))
    @MapKeyJoinColumn(name = "student_id")
    @Column(name = "score")
    private Map<Student, Double> performers = new HashMap<>();
    @ManyToMany
    private List<Student> registeredStudent = new ArrayList<>();
    @OneToOne
    private Student winner;
    @PastOrPresent
    private LocalDateTime created = LocalDateTime.now();
    @PastOrPresent
    private LocalDateTime modified = LocalDateTime.now();
    @NonNull
    private LocalDateTime startDate;
    @NonNull
    private LocalDateTime endDate;
    @OneToOne
    private Certificate certificateReward;

    public Competition(int startDay, int startMonth, int startYear, int startHour, int startMinutes, int endDay, int endMonth, int endHour, int endMinutes, List<CompetitionQuestion> questions, int pointsPerQuestion) {
        startDate = LocalDateTime.parse(String.format("%02d-%02d-%02dT%02d:%02d:00", startYear, startMonth, startDay, startHour, startMinutes));
        endDate = LocalDateTime.parse(String.format("%02d-%02d-%02dT%02d:%02d:00", startYear, endMonth, endDay, endHour, endMinutes));
        this.questions = questions;
        this.COMPETITION_QUESTION_POINTS = pointsPerQuestion;
    }

    public void executeCompetitionTest(Student performer, List<Integer> performerAnswers) {
        int totalScore = 0;
        List<CompetitionQuestion> competitionQuestions = new ArrayList<>(questions);
        for (int i = 0; i < questions.size(); i++) {
            boolean correctAnswer = competitionQuestions.get(i).isCorrect(performerAnswers.get(i));
            if (correctAnswer) {
                totalScore += COMPETITION_QUESTION_POINTS;
            }
        }
        double grade = (totalScore * 1.0 / (questions.size() * COMPETITION_QUESTION_POINTS)) * 6;
        if (grade < 2){
            grade = 2.00;
        }
        performers.put(performer, grade);
    }

    public static boolean isFinished(LocalDateTime endDate) {
        return LocalDateTime.now().isAfter(endDate);
    }

    public static boolean isStarted(LocalDateTime startDate) {
        return LocalDateTime.now().isAfter(startDate);
    }

    public User findWinner() throws NonexistingEntityException {
        var entity = performers.entrySet().stream().max(Map.Entry.comparingByValue()).orElseThrow(() -> new NonexistingEntityException(String.format(CommonMessages.THERE_IS_NO_WINNER, getId())));
        return entity.getKey();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Competition{");
        sb.append("id=").append(id);
        sb.append('}');
        return sb.toString();
    }

}
