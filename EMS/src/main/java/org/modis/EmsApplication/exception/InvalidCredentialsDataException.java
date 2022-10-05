package org.modis.EmsApplication.exception;

import java.util.List;

public class InvalidCredentialsDataException extends RuntimeException {
    private List<String> constraintViolations = List.of();
    public InvalidCredentialsDataException() {
    }

    public InvalidCredentialsDataException(String message) {
        super(message);
    }

    public InvalidCredentialsDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCredentialsDataException(Throwable cause) {
        super(cause);
    }

    public InvalidCredentialsDataException(List<String> constraintViolations) {
        this.constraintViolations = constraintViolations;
    }

    public InvalidCredentialsDataException(String message, List<String> constraintViolations) {
        super(message);
        this.constraintViolations = constraintViolations;
    }

    public InvalidCredentialsDataException(String message, Throwable cause, List<String> constraintViolations) {
        super(message, cause);
        this.constraintViolations = constraintViolations;
    }

    public InvalidCredentialsDataException(Throwable cause, List<String> constraintViolations) {
        super(cause);
        this.constraintViolations = constraintViolations;
    }

    public List<String> getConstraintViolations() {
        return constraintViolations;
    }
}

