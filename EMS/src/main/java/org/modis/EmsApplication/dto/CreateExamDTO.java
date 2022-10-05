package org.modis.EmsApplication.dto;

import lombok.Data;
import lombok.NonNull;
import org.modis.EmsApplication.model.Exam;
import org.modis.EmsApplication.model.enums.Subject;

@Data
public class CreateExamDTO {
    @NonNull
    private Subject subject;
    @NonNull
    private String content;

}
