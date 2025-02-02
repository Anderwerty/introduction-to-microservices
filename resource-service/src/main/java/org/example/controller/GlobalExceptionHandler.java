package org.example.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.example.service.exception.*;
import org.example.service.dto.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    @ExceptionHandler({ConstraintViolationException.class,
            IllegalResourceException.class,
            NotValidSongMetaDataRuntimeException.class,
            IllegalArgumentException.class,
            ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage notValidResource(RuntimeException exception) {
        log.error(exception);
        if (exception instanceof ConstraintViolationException e) {
            Map<String, String> details = e.getConstraintViolations().stream()
                    .collect(Collectors.toMap(x -> x.getPropertyPath().toString(),
                            ConstraintViolation::getMessage, (a, b) -> b));
            return new ErrorMessage(HttpStatus.BAD_REQUEST.value(), "Validation error", details);
        }

        return new ErrorMessage(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage resourceNotFound(ResourceNotFoundException exception) {
        log.error(exception);
        return new ErrorMessage(HttpStatus.NOT_FOUND.value(), exception.getMessage());
    }

    @ExceptionHandler(ConflictRuntimeException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessage conflict(ConflictRuntimeException exception) {
        log.error(exception);
        return exception.getErrorMessage();
    }

}
