package org.example.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.example.service.dto.SimpleErrorResponse;
import org.example.service.dto.ValidationErrorResponse;
import org.example.service.exception.ConflictRuntimeException;
import org.example.service.exception.IllegalResourceException;
import org.example.service.exception.NotValidSongMetaDataRuntimeException;
import org.example.service.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SimpleErrorResponse badRequest(ConstraintViolationException exception) {
        log.debug(exception);

        String message = exception.getConstraintViolations().stream()
                .findAny()
                .map(ConstraintViolation::getMessage)
                .orElse(null);
        return new SimpleErrorResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()), message);
    }

    @ExceptionHandler({IllegalResourceException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SimpleErrorResponse badRequestOnIllegalResourceException(IllegalResourceException exception) {
        log.debug(exception);

        return new SimpleErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }

    @ExceptionHandler({NotValidSongMetaDataRuntimeException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse badRequestOnNotValidSongMetaDataRuntimeException(NotValidSongMetaDataRuntimeException exception) {
        log.debug(exception);

        return new ValidationErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getValidationErrorResponse().getDetails());
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
