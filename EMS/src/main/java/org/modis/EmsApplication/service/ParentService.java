package org.modis.EmsApplication.service;

import org.modis.EmsApplication.dto.*;
import org.modis.EmsApplication.exception.InvalidEntityDataException;
import org.modis.EmsApplication.exception.InvalidOperationException;
import org.modis.EmsApplication.exception.NonexistingEntityException;
import org.modis.EmsApplication.model.Student;
import org.modis.EmsApplication.model.User;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface ParentService {

    List<ParentExposeDTO> getAllParents();

    ParentExposeDTO getParentById(Long id) throws NonexistingEntityException;

    List<StudentExposeDTO> getAllChildren(Long id);

    StudentExposeDTO addChild(Long parentId, Long studentId) throws InvalidOperationException;


    StudentExposeDTO removeChild(Long parentId, Long studentId) throws InvalidOperationException;

    String parentsCount();
}
