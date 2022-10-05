package org.modis.EmsApplication.dto;

import lombok.Data;
import lombok.NonNull;
import org.modis.EmsApplication.exception.InvalidEntityDataException;
import org.modis.EmsApplication.model.Teacher;
import org.modis.EmsApplication.model.User;
import org.modis.EmsApplication.model.enums.Gender;
import org.modis.EmsApplication.model.enums.Role;
import org.modis.EmsApplication.model.enums.Subject;
import org.modis.EmsApplication.utils.CommonMessages;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.lang.reflect.InvocationTargetException;

@Data
public class CreateUserDTO {
    @NotNull
    @Size(min = 2, max = 30)
    @NonNull
    private String firstName;
    @NotNull
    @Size(min = 2, max = 30)
    @NonNull
    private String lastName;
    @NotNull
    @Email
    @NonNull
    private String email;
    @NotNull
    private Gender gender;
    private Subject subject;
    @NotNull
    private Role role;

    public static User parseUser(CreateUserDTO userDTO) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        String className = userDTO.getRole().name().charAt(0) + userDTO.getRole().name().substring(1).toLowerCase();
        Class<?> modelClass = Class.forName("org.modis.EmsApplication.model.".concat(className));
        User model = (User) modelClass.getDeclaredConstructor().newInstance();
        model.setFirstName(userDTO.getFirstName());
        model.setLastName(userDTO.getLastName());
        model.setEmail(userDTO.getEmail());
        model.setGender(userDTO.getGender());
        model.setRole(userDTO.getRole());
        if (model instanceof Teacher) {
            if (userDTO.getSubject() == null) {
                throw new InvalidEntityDataException(String.format(CommonMessages.MUST_ADD_SUBJECT_TO_TEACHERS, model.getId()));
            }
            ((Teacher) model).setSubject(userDTO.getSubject());
        }
        return model;
    }

}


