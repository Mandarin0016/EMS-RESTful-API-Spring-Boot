package org.modis.EmsApplication.service;

import org.modis.EmsApplication.dto.CreateUserDTO;
import org.modis.EmsApplication.dto.UpdateUserDTO;
import org.modis.EmsApplication.dto.UserExposeDTO;
import org.modis.EmsApplication.exception.InvalidEntityDataException;
import org.modis.EmsApplication.exception.NonexistingEntityException;
import org.modis.EmsApplication.model.User;

import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface UserService {

    List<UserExposeDTO> getAllUsers();

    List<User> getAllUsersModels();

    UserExposeDTO getUserById(Long id) throws NonexistingEntityException;

    User getUserByIdModel(Long id) throws NonexistingEntityException;

    User getUserByEmail(String email) throws NonexistingEntityException;

    UserExposeDTO create(CreateUserDTO userDTO) throws InvalidEntityDataException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, NoSuchAlgorithmException;

    UserExposeDTO update(UpdateUserDTO userDTO) throws InvalidEntityDataException, NonexistingEntityException;

    User updateModel(User user) throws InvalidEntityDataException, NonexistingEntityException;

    UserExposeDTO deleteById(Long id) throws NonexistingEntityException;

    String usersCount();
}
