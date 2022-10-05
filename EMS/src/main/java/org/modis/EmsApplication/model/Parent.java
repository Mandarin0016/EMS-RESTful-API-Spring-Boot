package org.modis.EmsApplication.model;

import lombok.*;
import org.modis.EmsApplication.model.enums.AccountStatus;
import org.modis.EmsApplication.model.enums.Gender;
import org.modis.EmsApplication.model.enums.Role;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Parent extends User {

    @ManyToMany(mappedBy = "parentsId", targetEntity = Student.class)
    private List<Student> children = new ArrayList<>();

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Parent{");
        sb.append("id=").append(getId());
        sb.append('}');
        return sb.toString();
    }
}
