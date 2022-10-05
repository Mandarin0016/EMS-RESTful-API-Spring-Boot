package org.modis.EmsApplication.web;

import org.modis.EmsApplication.dto.CreateUserDTO;
import org.modis.EmsApplication.dto.UpdateUserDTO;
import org.modis.EmsApplication.dto.UserExposeDTO;
import org.modis.EmsApplication.model.User;
import org.modis.EmsApplication.exception.InvalidEntityDataException;
import org.modis.EmsApplication.exception.NonexistingEntityException;
import org.modis.EmsApplication.service.UserService;
import org.modis.EmsApplication.utils.CommonMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static org.modis.EmsApplication.utils.ErrorHandlingUtils.handleValidationErrors;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserExposeDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id:\\d+}")
    public UserExposeDTO getUserById(@PathVariable("id") Long id) throws NonexistingEntityException {
        return userService.getUserById(id);
    }

    @PostMapping
    public ResponseEntity<UserExposeDTO> createNewUser(@Valid @RequestBody CreateUserDTO createUserDTO, Errors errors) throws InvalidEntityDataException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, NoSuchAlgorithmException {
        handleValidationErrors(errors);
        UserExposeDTO created = userService.create(createUserDTO);

        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest().pathSegment("{id}")
                        .buildAndExpand(created.getId()).toUri()
        ).body(created);
    }

    @PutMapping("/{id:\\d+}")
    public UserExposeDTO updateUser(@PathVariable("id") Long id, @Valid @RequestBody UpdateUserDTO userDTO, Errors errors) throws InvalidEntityDataException, NonexistingEntityException {
        handleValidationErrors(errors);

        if(!id.equals(userDTO.getId())) {
            throw new InvalidEntityDataException(
                    String.format(CommonMessages.DIFFERENT_ID_IN_REQUEST_BODY_AND_URL, id, userDTO.getId()));
        }
        return userService.update(userDTO);
    }

    @DeleteMapping("{id:\\d+}")
    public UserExposeDTO deleteById(@PathVariable("id") Long id) throws NonexistingEntityException {
        return userService.deleteById(id);
    }

    @GetMapping("/count")
    public String getUsersCount(){
        return userService.usersCount();
    }



}
