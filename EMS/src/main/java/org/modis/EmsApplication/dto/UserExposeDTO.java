package org.modis.EmsApplication.dto;

import lombok.Data;
import org.modis.EmsApplication.model.enums.AccountStatus;
import org.modis.EmsApplication.model.enums.Gender;
import org.modis.EmsApplication.model.enums.Role;

import java.time.LocalDateTime;

@Data
public class UserExposeDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Gender gender;
    private Role role;
    private AccountStatus accountStatus;
    private LocalDateTime created;
    private LocalDateTime modified;
}
