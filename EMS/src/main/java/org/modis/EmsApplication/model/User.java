package org.modis.EmsApplication.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.modis.EmsApplication.model.enums.AccountStatus;
import org.modis.EmsApplication.model.enums.Gender;
import org.modis.EmsApplication.model.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
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
    @NonNull
    private String email;
    @Size(min = 8)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(columnDefinition = "LONGTEXT")
    private String password;
    @NotNull
    @Enumerated(EnumType.STRING)
    @NonNull
    private Gender gender;
    @Enumerated(EnumType.STRING)
    private Role role;
    private boolean isActive = true;
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;
    @PastOrPresent
    private LocalDateTime created = LocalDateTime.now();
    @PastOrPresent
    private LocalDateTime modified = LocalDateTime.now();

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id=").append(id);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isActive;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isActive;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
