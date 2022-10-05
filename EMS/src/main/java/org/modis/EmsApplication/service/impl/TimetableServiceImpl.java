package org.modis.EmsApplication.service.impl;

import org.modelmapper.ModelMapper;
import org.modis.EmsApplication.dao.SchoolYearRepository;
import org.modis.EmsApplication.dao.TimetableRepository;
import org.modis.EmsApplication.dao.UserRepository;
import org.modis.EmsApplication.dto.TimetableDTO;
import org.modis.EmsApplication.dto.UpdateTimetableDTO;
import org.modis.EmsApplication.exception.InvalidEntityDataException;
import org.modis.EmsApplication.exception.NonexistingEntityException;
import org.modis.EmsApplication.model.*;
import org.modis.EmsApplication.service.TimetableService;
import org.modis.EmsApplication.utils.CommonMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


@Service
@Transactional
public class TimetableServiceImpl implements TimetableService {

    private final TimetableRepository timetableRepository;
    private final UserRepository userRepository;
    private final SchoolYearRepository schoolYearRepository;
    private final ModelMapper mapper = new ModelMapper();

    @Autowired
    public TimetableServiceImpl(TimetableRepository timetableRepository, UserRepository userRepository, SchoolYearRepository schoolYearRepository) {
        this.timetableRepository = timetableRepository;
        this.userRepository = userRepository;
        this.schoolYearRepository = schoolYearRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<TimetableDTO> getAllTimetables() {
        return timetableRepository.findAll().stream().map(timetable -> mapper.map(timetable, TimetableDTO.class)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TimetableDTO getTimetableById(Long id) throws NonexistingEntityException {
        return timetableRepository.findById(id).map(timetable -> mapper.map(timetable, TimetableDTO.class)).orElseThrow(() -> new NonexistingEntityException(String.format(CommonMessages.TIMETABLE_DOES_NOT_EXISTS, id)));
    }

    @Override
    public Timetable getTimetableByIdModel(Long id) throws NonexistingEntityException {
        return timetableRepository.findById(id).orElseThrow(() -> new NonexistingEntityException(String.format(CommonMessages.TIMETABLE_DOES_NOT_EXISTS, id)));
    }

    @Override
    public TimetableDTO create(TimetableDTO timetableDTO) throws InvalidEntityDataException {
        Timetable timetable = mapper.map(timetableDTO, Timetable.class);
        if (timetableRepository.findByTitle(timetableDTO.getTitle()).isPresent()) {
            throw new InvalidEntityDataException(String.format(CommonMessages.ALREADY_EXISTING_TIMETABLE_TITLE, timetable.getTitle()));
        }
        if (timetableRepository.findByContentUrl(timetableDTO.getContentUrl()).isPresent()) {
            throw new InvalidEntityDataException(String.format(CommonMessages.ALREADY_EXISTING_TIMETABLE_CONTENT_URL, timetable.getContentUrl()));
        }
        return mapper.map(timetableRepository.save(timetable), TimetableDTO.class);
    }

    @Override
    public TimetableDTO update(Long id, UpdateTimetableDTO timetableDTO) throws NonexistingEntityException {
        Timetable oldTimetable = getTimetableByIdModel(id);
        Timetable updateTimetable = mapper.map(timetableDTO, Timetable.class);
        checkExistingTimetable(timetableDTO, oldTimetable, updateTimetable);
        return mapper.map(timetableRepository.save(updateTimetable), TimetableDTO.class);
    }

    private void checkExistingTimetable(UpdateTimetableDTO timetableDTO, Timetable oldTimetable, Timetable updateTimetable) {
        if (timetableRepository.findByTitle(updateTimetable.getTitle()).isPresent() && !Objects.equals(timetableRepository.findByTitle(updateTimetable.getTitle()).get().getId(), oldTimetable.getId())) {
            throw new InvalidEntityDataException(String.format(CommonMessages.ALREADY_EXISTING_TIMETABLE_TITLE, timetableDTO.getTitle()));
        }
        if (timetableRepository.findByContentUrl(timetableDTO.getContentUrl()).isPresent() && !oldTimetable.getId().equals(updateTimetable.getId())) {
            throw new InvalidEntityDataException(String.format(CommonMessages.ALREADY_EXISTING_TIMETABLE_CONTENT_URL, timetableDTO.getContentUrl()));
        }
        updateTimetable.setCreated(oldTimetable.getCreated());
        updateTimetable.setModified(LocalDateTime.now());
    }

    @Override
    public Timetable updateModel(Long id, UpdateTimetableDTO timetableDTO) throws NonexistingEntityException {
        Timetable updateTimetable = mapper.map(timetableDTO, Timetable.class);
        Timetable oldTimetable = getTimetableByIdModel(id);
        checkExistingTimetable(timetableDTO, oldTimetable, updateTimetable);
        return timetableRepository.save(updateTimetable);
    }

    @Override
    public TimetableDTO deleteById(Long id) throws NonexistingEntityException {
        Timetable oldTimetable = getTimetableByIdModel(id);
        List<SchoolYear> sy = schoolYearRepository.findAll();
        for (SchoolYear schoolYear : sy) {
            if (schoolYear.getTimetable() != null && schoolYear.getTimetable().equals(oldTimetable)) {
                schoolYearRepository.cleanSchoolYearTimetable(schoolYear.getId());
            }
        }
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user instanceof Student) {
                if (((Student) user).getTimetable() == null) {
                    continue;
                }
                if (((Student) user).getTimetable().equals(oldTimetable)) {
                    userRepository.cleanUserTimetable(user.getId());
                }
            }
            if (user instanceof Teacher) {
                if (((Teacher) user).getTimetable() == null) {
                    continue;
                }
                if (((Teacher) user).getTimetable().equals(oldTimetable)) {
                    userRepository.cleanUserTimetable(user.getId());
                }
            }
        }
        timetableRepository.delete(oldTimetable);
        return mapper.map(oldTimetable, TimetableDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public String count() {
        return String.valueOf(timetableRepository.count());
    }
}
