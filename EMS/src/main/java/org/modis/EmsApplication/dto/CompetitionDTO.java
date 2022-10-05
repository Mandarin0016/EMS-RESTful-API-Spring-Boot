package org.modis.EmsApplication.dto;

import lombok.Data;
import lombok.NonNull;
import org.modis.EmsApplication.model.Competition;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Data
public class CompetitionDTO {

    @NonNull
    @Min(1)
    @Max(31)
    private Integer startDay;
    @NonNull
    @Min(1)
    @Max(12)
    private Integer startMonth;
    @NonNull
    @Min(2022)
    @Max(2050)
    private Integer startYear;
    @NonNull
    @Min(0)
    @Max(23)
    private Integer startHour;
    @NonNull
    @Min(0)
    @Max(59)
    private Integer startMinutes;
    @NonNull
    @Min(1)
    @Max(31)
    private Integer endDay;
    @NonNull
    @Min(1)
    @Max(12)
    private Integer endMonth;
    @NonNull
    @Min(0)
    @Max(23)
    private Integer endHour;
    @NonNull
    @Min(0)
    @Max(59)
    private Integer endMinutes;
    @NonNull
    private List<CompetitionQuestionDTO> questions;
    @NonNull
    @Min(1)
    @Max(100)
    private Integer pointsPerQuestion;

    public static Competition parseDTO(CompetitionDTO competitionDTO) {
        return new Competition(competitionDTO.getStartDay(), competitionDTO.getStartMonth(), competitionDTO.getStartYear(), competitionDTO.getStartHour(),
                competitionDTO.getStartMinutes(), competitionDTO.getEndDay(), competitionDTO.getEndMonth(), competitionDTO.getEndHour(), competitionDTO.getEndMinutes(), competitionDTO.getQuestions().stream().map(CompetitionQuestionDTO::parseDTO).toList(), competitionDTO.getPointsPerQuestion());
    }

}
