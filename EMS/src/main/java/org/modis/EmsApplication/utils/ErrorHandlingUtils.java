package org.modis.EmsApplication.utils;

import org.modis.EmsApplication.exception.InvalidEntityDataException;
import org.springframework.validation.Errors;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ErrorHandlingUtils {
    public static void handleValidationErrors(Errors errors) {
        if(errors.hasErrors()) {
            List<String> fieldErrorMessages = errors.getFieldErrors().stream()
                    .map(err -> String.format("%s for: '%s' = '%s'", err.getDefaultMessage(), err.getField(), err.getRejectedValue())).toList();
            List<String> errorMessages = new ArrayList<>(fieldErrorMessages);
            List<String> globalErrorMessages = errors.getGlobalErrors().stream()
                    .map(err -> String.format("%s", err.getDefaultMessage())).toList();
            errorMessages.addAll(globalErrorMessages);
            throw new InvalidEntityDataException(CommonMessages.INVALID_DATA_PROVIDED, errorMessages);
        }
    }
}
