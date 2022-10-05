package org.modis.EmsApplication.dto;

import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.URL;
import org.modis.EmsApplication.dao.TimetableRepository;
import org.modis.EmsApplication.exception.InvalidEntityDataException;
import org.modis.EmsApplication.model.Timetable;
import org.modis.EmsApplication.utils.CommonMessages;

@Data
public class UpdateTimetableDTO {
    @NonNull
    private Long id;
    @NonNull
    private String title;
    @NonNull
    @URL
    private String contentUrl;

    public static Timetable parseDTO(UpdateTimetableDTO timetableDTO, Timetable timetable, TimetableRepository timetableRepository) {
        if (timetableRepository.findByTitle(timetableDTO.getTitle()).isPresent()) {
            throw new InvalidEntityDataException(
                    String.format(CommonMessages.ALREADY_EXISTING_TIMETABLE_TITLE, timetableDTO.getTitle()));
        }
        timetable.setTitle(timetableDTO.getTitle());
        timetableDTO.setContentUrl(timetable.getContentUrl());
        return timetable;
    }
}
