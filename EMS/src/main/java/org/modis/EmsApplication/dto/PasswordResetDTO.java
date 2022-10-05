package org.modis.EmsApplication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetDTO {
    @NotNull
    @Email
    @NonNull
    private String email;
    @NotBlank
    private String oldPassword;
    @NotBlank
    private String newPassword;
}
