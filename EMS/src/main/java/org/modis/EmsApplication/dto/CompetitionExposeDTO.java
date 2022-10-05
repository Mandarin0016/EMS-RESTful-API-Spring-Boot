package org.modis.EmsApplication.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class CompetitionExposeDTO {

    private Long id;
    private List<CompetitionQuestionDTO> questions;
    private Integer COMPETITION_QUESTION_POINTS;
    private Map<String, Double> performers;
    private List<UserExposeDTO> registeredStudent;
    private UserExposeDTO winner;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private CertificateExposeDTO certificateReward;
    private LocalDateTime created;
    private LocalDateTime modified;

}
