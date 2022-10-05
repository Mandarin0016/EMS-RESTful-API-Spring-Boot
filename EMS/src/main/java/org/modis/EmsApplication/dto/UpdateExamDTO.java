package org.modis.EmsApplication.dto;

import lombok.Data;
import lombok.NonNull;
import org.modis.EmsApplication.model.Exam;
import org.modis.EmsApplication.model.enums.Subject;

@Data
public class UpdateExamDTO {

    @NonNull
    private Long id;
    @NonNull
    private Subject subject;
    @NonNull
    private String questions;
//
//    public static Exam parseDTO(UpdateExamDTO examDTO) {
//        Exam exam = new Exam(examDTO.getSubject(), examDTO.getQuestions());
//        exam.setId(examDTO.getId());
//        return exam;
//    }

}
