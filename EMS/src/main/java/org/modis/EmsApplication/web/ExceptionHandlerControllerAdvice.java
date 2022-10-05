package org.modis.EmsApplication.web;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import org.modis.EmsApplication.dto.ErrorResponseDTO;
import org.modis.EmsApplication.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mail.MailSendException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice {
    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> handleNonexistingEntityException(NonexistingEntityException ex) {
        return ResponseEntity.status(NOT_FOUND).body(new ErrorResponseDTO(NOT_FOUND.value(), ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> handleInvalidEntityDataException(InvalidEntityDataException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponseDTO(BAD_REQUEST.value(), ex.getMessage(), ex.getConstraintViolations()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> handleInvalidOperationException(InvalidOperationException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponseDTO(BAD_REQUEST.value(), ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> handleValueInstantiationException(ValueInstantiationException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponseDTO(BAD_REQUEST.value(), ex.getCause().getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> handleInvalidFormatException(InvalidFormatException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponseDTO(BAD_REQUEST.value(), "Wrong value for '" + ex.getValue() + "'. Must be one of the following: " + Arrays.toString(Arrays.stream(ex.getTargetType().getDeclaredFields()).map(Field::getName).filter(name -> !name.equals("$VALUES")).toArray())));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponseDTO(BAD_REQUEST.value(), Objects.requireNonNull(ex.getMessage())));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponseDTO(BAD_REQUEST.value(), String.format("Missing parameter '%s' of type '%s'.", ex.getParameterName(), ex.getParameterType())));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponseDTO(BAD_REQUEST.value(), ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> handleUnautorizedException(UnautorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDTO(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> handleInsufficientPrivilegiesException(InsufficientPrivilegiesException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponseDTO(HttpStatus.FORBIDDEN.value(), ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> handleInvalidCredentialsDataException(InvalidCredentialsDataException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDTO(HttpStatus.UNAUTHORIZED.value(), "InvalidCredentialsDataException: " + ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponseDTO(HttpStatus.FORBIDDEN.value(), ex.getMessage() + "!"));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> handleMailSendException(MailSendException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Due to the free email services we are using, something went wrong... " + ex.getMessage() + "!" + "Please wait a few seconds and try again."));
    }

}
