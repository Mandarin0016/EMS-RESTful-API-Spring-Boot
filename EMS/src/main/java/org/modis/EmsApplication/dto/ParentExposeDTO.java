package org.modis.EmsApplication.dto;

import lombok.Data;
import org.modelmapper.ModelMapper;
import org.modis.EmsApplication.model.*;
import org.modis.EmsApplication.model.enums.Gender;
import org.modis.EmsApplication.model.enums.Role;

import java.util.List;

@Data
public class ParentExposeDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Gender gender;
    private Role role;
    private List<UserExposeDTO> children;

    public ParentExposeDTO(User model) {
        this.setId(model.getId());
        this.setFirstName(model.getFirstName());
        this.setLastName(model.getLastName());
        this.setEmail(model.getEmail());
        this.setGender(model.getGender());
        this.setRole(model.getRole());
    }


    public static ParentExposeDTO parseModel(Parent model) {
        ModelMapper mapper = new ModelMapper();
        ParentExposeDTO parentExposeDTO = new ParentExposeDTO(model);
        parentExposeDTO.setChildren(model.getChildren().stream().map(student -> mapper.map(student, UserExposeDTO.class)).toList());
        return parentExposeDTO;
    }
}
