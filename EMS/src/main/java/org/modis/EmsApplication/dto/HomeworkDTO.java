package org.modis.EmsApplication.dto;

import lombok.Data;
import lombok.NonNull;
import org.modis.EmsApplication.model.enums.Subject;

@Data
public class HomeworkDTO {
    private Long id;
    @NonNull
    private String description;
    @NonNull
    private Subject subject;

    public HomeworkDTO() {
    }
}
