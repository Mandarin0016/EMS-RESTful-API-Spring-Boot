package org.modis.EmsApplication.service.impl;

import org.modis.EmsApplication.dao.UserRepository;
import org.modis.EmsApplication.dto.ParentExposeDTO;
import org.modis.EmsApplication.dto.StudentExposeDTO;
import org.modis.EmsApplication.exception.InvalidEntityDataException;
import org.modis.EmsApplication.exception.InvalidOperationException;
import org.modis.EmsApplication.exception.NonexistingEntityException;
import org.modis.EmsApplication.model.Parent;
import org.modis.EmsApplication.model.User;
import org.modis.EmsApplication.service.ParentService;
import org.modis.EmsApplication.service.StudentService;
import org.modis.EmsApplication.utils.CommonMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ParentServiceImpl implements ParentService {

    private final UserRepository userRepository;
    private final StudentService studentService;

    @Autowired
    public ParentServiceImpl(UserRepository userRepository, StudentService studentService) {
        this.userRepository = userRepository;
        this.studentService = studentService;
    }


    @Override
    @Transactional(readOnly = true)
    public List<ParentExposeDTO> getAllParents() {
        return userRepository.findAll().stream().filter(model -> model instanceof Parent).map(parent -> ParentExposeDTO.parseModel((Parent) parent)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ParentExposeDTO getParentById(Long id) throws NonexistingEntityException {
        User user = userRepository.findById(id).orElseThrow(() -> new NonexistingEntityException(
                String.format(CommonMessages.USER_DOES_NOT_EXISTS, id)
        ));
        if (!(user instanceof Parent)) {
            throw new InvalidEntityDataException(String.format(CommonMessages.INVALID_USER_TYPE, user.getId(), Parent.class.getSimpleName()));
        }
        return ParentExposeDTO.parseModel((Parent) user);
    }

    private User getParentByIdModel(Long id) throws NonexistingEntityException {
        User user = userRepository.findById(id).orElseThrow(() -> new NonexistingEntityException(
                String.format(CommonMessages.USER_DOES_NOT_EXISTS, id)
        ));
        if (!(user instanceof Parent)) {
            throw new InvalidEntityDataException(String.format(CommonMessages.INVALID_USER_TYPE, user.getId(), Parent.class.getSimpleName()));
        }
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentExposeDTO> getAllChildren(Long id) {
        User parent = getParentByIdModel(id);
        return ((Parent) parent).getChildren().stream().map(StudentExposeDTO::parseModel).toList();
    }

    @Override
    public StudentExposeDTO addChild(Long parentId, Long studentId) throws InvalidOperationException {
        return studentService.addStudentParent(studentId, parentId);
    }

    @Override
    public StudentExposeDTO removeChild(Long parentId, Long studentId) throws InvalidOperationException {
        return studentService.deleteStudentParent(studentId, parentId);
    }

    @Override
    @Transactional(readOnly = true)
    public String parentsCount() {
        return String.valueOf(userRepository.findAll().stream().filter(model -> model instanceof Parent).toList().size());
    }
}
