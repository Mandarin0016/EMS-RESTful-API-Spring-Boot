package org.modis.EmsApplication.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SchoolYearDeleteDTO {

    final String SCHOOL_YEAR_DELETED_SUCCESSFULLY;
    final LocalDateTime deleted_on = LocalDateTime.now();


    public SchoolYearDeleteDTO() {
        SCHOOL_YEAR_DELETED_SUCCESSFULLY = "School year was successfully deleted!";
    }

    public SchoolYearDeleteDTO(String deletedSchoolYearId) {
        SCHOOL_YEAR_DELETED_SUCCESSFULLY = String.format("School year with ID='%s' was successfully deleted!", deletedSchoolYearId);
    }
}
