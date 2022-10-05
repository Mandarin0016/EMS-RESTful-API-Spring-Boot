package org.modis.EmsApplication.dto;

import lombok.Data;
import lombok.NonNull;
import org.modis.EmsApplication.dao.UserRepository;
import org.modis.EmsApplication.exception.InvalidEntityDataException;
import org.modis.EmsApplication.model.Teacher;
import org.modis.EmsApplication.model.User;
import org.modis.EmsApplication.model.enums.Gender;
import org.modis.EmsApplication.model.enums.Subject;
import org.modis.EmsApplication.utils.CommonMessages;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UpdateUserDTO {

    @NotNull
    private Long id;
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
    private String email;
    @NotNull
    private Gender gender;
    private Subject subject;

    public static void parseUser(UpdateUserDTO userDTO, User user, UserRepository userRepository) {
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()){
            throw new InvalidEntityDataException(CommonMessages.EMAIL_ALREADY_EXISTING);
        }
        user.setEmail(userDTO.getEmail());
        user.setGender(userDTO.getGender());
        if (user instanceof Teacher && userDTO.getSubject() != null){
            ((Teacher) user).setSubject(userDTO.getSubject());
        }
        user.setModified(LocalDateTime.now());
    }
}
