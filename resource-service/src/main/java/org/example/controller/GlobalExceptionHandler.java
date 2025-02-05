package org.example.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.extern.log4j.Log4j2;
import org.example.service.dto.SimpleErrorResponse;
import org.example.service.exception.*;
import org.example.service.dto.ValidationErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    @ExceptionHandler({
            IllegalResourceException.class,
            IllegalParameterException.class,
            NotValidSongMetaDataRuntimeException.class,
            ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse notValidResource(RuntimeException exception) {
        log.debug(exception);
        if (exception instanceof ConstraintViolationException e) {
            Map<String, String> details = e.getConstraintViolations().stream()
                    .collect(Collectors.toMap(x -> getLastNode(x.getPropertyPath()).toString(),
                            ConstraintViolation::getMessage, (a, b) -> b));
            return new ValidationErrorResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()), details);
        }
        if (exception instanceof NotValidSongMetaDataRuntimeException e) {
            return new ValidationErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getValidationErrorResponse().getDetails());
        }

        if (exception instanceof IllegalParameterException e) {
            return new ValidationErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getDetails());
        }

        return new ValidationErrorResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()), ((IllegalResourceException) exception).getDetails());
    }


    private static Path.Node getLastNode(Path propertyPath) {
        Path.Node lastNode = null;
        for (Path.Node node : propertyPath) {
            lastNode = node;
        }
        return lastNode;
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public SimpleErrorResponse resourceNotFound(ResourceNotFoundException exception) {
        log.debug(exception);
        return new SimpleErrorResponse(HttpStatus.NOT_FOUND.value(), exception.getMessage());
    }

    @ExceptionHandler(ConflictRuntimeException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public SimpleErrorResponse conflict(ConflictRuntimeException exception) {
        log.debug(exception);
        return exception.getErrorResponse();
    }

}
