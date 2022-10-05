package org.modis.EmsApplication.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.modis.EmsApplication.model.CompetitionQuestion;

@Data
@NoArgsConstructor
public class CompetitionQuestionDTO {
    @NonNull
    private String title;
    @NonNull
    private String answers;
    @NonNull
    private int realAnswer;

    public CompetitionQuestionDTO(String title, String answers, int realAnswer) {
        this.title = title;
        this.answers = answers;
        this.realAnswer = realAnswer;
    }

    public static CompetitionQuestion parseDTO(CompetitionQuestionDTO questionDTO) {
        return new CompetitionQuestion(questionDTO.getTitle(), questionDTO.getAnswers(), questionDTO.getRealAnswer());
    }

    public static CompetitionQuestionDTO parseModel(CompetitionQuestion model) {
        return new CompetitionQuestionDTO(model.getTitle(), model.getAnswers(), model.getRealAnswer());
    }
}
