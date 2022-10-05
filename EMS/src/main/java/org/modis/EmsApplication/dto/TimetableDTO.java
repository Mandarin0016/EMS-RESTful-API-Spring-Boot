package org.modis.EmsApplication.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
public class TimetableDTO {

    private Long id;
    @NonNull
    private String title;
    @NonNull
    @URL
    private String contentUrl;
//
//    public static Timetable parseDTO(TimetableDTO timetableDTO) {
//        Timetable timetable = new Timetable();
//        timetable.setTitle(timetable.getTitle());
//        timetable.setContentUrl(timetableDTO.getContentUrl());
//        return timetable;
//    }
}
