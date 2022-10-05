package org.modis.EmsApplication.web;

import org.modelmapper.ModelMapper;
import org.modis.EmsApplication.dto.CredentialsDTO;
import org.modis.EmsApplication.dto.LoginResponseDTO;
import org.modis.EmsApplication.dto.PasswordResetDTO;
import org.modis.EmsApplication.dto.UserExposeDTO;
import org.modis.EmsApplication.dto.email.EmailDetails;
import org.modis.EmsApplication.exception.InvalidCredentialsDataException;
import org.modis.EmsApplication.exception.InvalidOperationException;
import org.modis.EmsApplication.model.User;
import org.modis.EmsApplication.model.enums.AccountStatus;
import org.modis.EmsApplication.service.EmailService;
import org.modis.EmsApplication.service.UserService;
import org.modis.EmsApplication.utils.CommonMessages;
import org.modis.EmsApplication.web.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static org.modis.EmsApplication.utils.ErrorHandlingUtils.handleValidationErrors;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final ModelMapper mapper = new ModelMapper();
    private final JwtUtils jwtUtils;
    private final EmailService emailService;

    @Autowired
    public AuthController(UserService userService, JwtUtils jwtUtils, EmailService emailService) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.emailService = emailService;
    }

    @PostMapping("/resetPassword")
    public UserExposeDTO resetPassword(@Valid @RequestBody PasswordResetDTO passwordResetDTO, Errors errors) throws NoSuchAlgorithmException {
        handleValidationErrors(errors);
        User user = userService.getUserByEmail(passwordResetDTO.getEmail());

        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] rawOld = md.digest(passwordResetDTO.getOldPassword().getBytes(StandardCharsets.UTF_8));
        final String hashBase64 = Base64.getEncoder().encodeToString(md.digest(rawOld));

        if (!hashBase64.equals(user.getPassword())) {
            throw new InvalidCredentialsDataException(CommonMessages.THE_GIVEN_PASSWORD_IS_INCORRECT);
        } else {
            byte[] newHashedPasswordRaw = md.digest(passwordResetDTO.getNewPassword().getBytes(StandardCharsets.UTF_8));
            final String newHashedPassword = Base64.getEncoder().encodeToString(md.digest(newHashedPasswordRaw));
            user.setPassword(newHashedPassword);
            user.setAccountStatus(AccountStatus.ACTIVE);
            EmailDetails emailDetails = new EmailDetails(CommonMessages.EMS_CREATION_SERVICE_MAILBOX, user.getEmail(), String.format(CommonMessages.PASSWORD_SUCCESSFULLY_CHANGED, user.getFirstName(), user.getLastName()), CommonMessages.SCHOOL_ACCOUNT_PASSWORD_SUCCESSFULLY_CHANGED);
            emailService.sendEmail(emailDetails);
            return mapper.map(userService.updateModel(user), UserExposeDTO.class);
        }
    }


    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody CredentialsDTO credentials, Errors errors) throws NoSuchAlgorithmException, InvalidOperationException {
        handleValidationErrors(errors);
        final User user = userService.getUserByEmail(credentials.getEmail());
        if (user.getAccountStatus().equals(AccountStatus.PASSWORD_RESET)) {
            throw new InvalidOperationException(String.format(CommonMessages.PASSWORD_RESET_ACCOUNT_STATUS, user.getEmail()));
        }

        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] raw = md.digest(credentials.getPassword().getBytes(StandardCharsets.UTF_8));
        final String hashBase64 = Base64.getEncoder().encodeToString(md.digest(raw));

        if (!hashBase64.equals(user.getPassword())) {
            throw new InvalidCredentialsDataException(CommonMessages.THE_GIVEN_PASSWORD_IS_INCORRECT);
        }
        final String token = jwtUtils.generateToken(user);
        return new LoginResponseDTO(token, user.getEmail());
    }
}












