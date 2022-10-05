package org.modis.EmsApplication.service.impl;

import org.modelmapper.ModelMapper;
import org.modis.EmsApplication.dto.TeacherExposeDTO;
import org.modis.EmsApplication.dto.TimetableDTO;
import org.modis.EmsApplication.exception.InvalidEntityDataException;
import org.modis.EmsApplication.exception.InvalidOperationException;
import org.modis.EmsApplication.exception.NonexistingEntityException;
import org.modis.EmsApplication.model.Teacher;
import org.modis.EmsApplication.model.Timetable;
import org.modis.EmsApplication.model.User;
import org.modis.EmsApplication.model.enums.Role;
import org.modis.EmsApplication.service.TeacherService;
import org.modis.EmsApplication.service.TimetableService;
import org.modis.EmsApplication.service.UserService;
import org.modis.EmsApplication.utils.CommonMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@Transactional
public class TeacherServiceImpl implements TeacherService {

    private final UserService userService;
    private final TimetableService timetableService;
    private final ModelMapper mapper = new ModelMapper();

    @Autowired
    public TeacherServiceImpl(UserService userService, TimetableService timetableService) {
        this.userService = userService;
        this.timetableService = timetableService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherExposeDTO> getAllTeachers() {
        return userService.getAllUsersModels().stream().filter(user -> user.getRole().equals(Role.TEACHER)).map(TeacherExposeDTO::parseModel).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherExposeDTO getTeacherById(Long id) {
        User user = userService.getUserByIdModel(id);
        if (!(user instanceof Teacher)) {
            throw new InvalidEntityDataException(String.format(CommonMessages.INVALID_USER_TYPE, user.getId(), Teacher.class.getSimpleName()));
        }
        return TeacherExposeDTO.parseModel(user);
    }

    @Override
    @Transactional(readOnly = true)
    public TimetableDTO getTimetable(Long teacherId) throws NonexistingEntityException, InvalidOperationException {
        User teacher = userService.getUserByIdModel(teacherId);
        if (teacher instanceof Teacher) {
            Timetable timetable = ((Teacher) teacher).getTimetable();
            if (timetable == null) {
                throw new NonexistingEntityException(String.format(CommonMessages.TEACHER_MISSING_TIMETABLE, teacher.getId()));
            }
            return mapper.map(timetable, TimetableDTO.class);
        } else {
            throw new InvalidOperationException(String.format(CommonMessages.INVALID_USER_TYPE, teacher.getId(), Teacher.class.getSimpleName()));
        }
    }

    @Override
    public TimetableDTO setTimetable(Long teacherId, Long timetableId) throws NonexistingEntityException, InvalidOperationException {
        User teacher = userService.getUserByIdModel(teacherId);
        Timetable timetable = timetableService.getTimetableByIdModel(timetableId);

        if (teacher instanceof Teacher) {
            ((Teacher) teacher).setTimetable(timetable);
        } else {
            throw new InvalidOperationException(String.format(CommonMessages.INVALID_USER_TYPE, teacher.getId(), teacher.getClass().getSimpleName()));
        }
        userService.updateModel(teacher);
        return mapper.map(timetable, TimetableDTO.class);
    }

    @Override
    public TimetableDTO deleteTimetable(Long teacherId) throws NonexistingEntityException, InvalidOperationException {
        User teacher = userService.getUserByIdModel(teacherId);
        Timetable timetable;
        if (teacher instanceof Teacher) {
            timetable = ((Teacher) teacher).getTimetable();
            if (timetable == null) {
                throw new NonexistingEntityException(String.format(CommonMessages.USER_DOES_NOT_HAVE_TIMETABLE, teacher.getId()));
            }
            ((Teacher) teacher).setTimetable(null);
        } else {
            throw new InvalidOperationException(String.format(CommonMessages.INVALID_USER_TYPE, teacher.getId(), teacher.getClass().getSimpleName()));
        }
        userService.updateModel(teacher);
        return mapper.map(timetable, TimetableDTO.class);
    }
}
