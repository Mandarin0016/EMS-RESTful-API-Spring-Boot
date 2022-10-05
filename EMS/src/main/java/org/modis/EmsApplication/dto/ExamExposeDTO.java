package org.modis.EmsApplication.dto;

import lombok.Data;
import org.modis.EmsApplication.model.*;
import org.modis.EmsApplication.model.enums.Subject;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class ExamExposeDTO {

    private Long id;
    private Subject subject;
    private String content;
    private Map<UserExposeDTO, String> submittedStudentAnswers;
    private Map<UserExposeDTO, Double> results;
    private LocalDateTime created;
    private LocalDateTime modified;

}
