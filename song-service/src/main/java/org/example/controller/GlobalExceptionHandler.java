package org.example.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.example.service.exception.NotFoundException;
import org.example.service.exception.SongAlreadyExistRuntimeException;
import org.example.service.rest.dto.SimpleErrorResponse;
import org.example.service.rest.dto.ValidationErrorResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse badRequestForMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        Map<String, String> details = exception.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField,
                        DefaultMessageSourceResolvable::getDefaultMessage, (a, b) -> b));
        return new ValidationErrorResponse(HttpStatus.BAD_REQUEST.value(), details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SimpleErrorResponse badRequestForConstraintViolationException(ConstraintViolationException exception) {
        String message = exception.getConstraintViolations().stream()
                .findAny()
                .map(ConstraintViolation::getMessage)
                .orElse(null);
        return new SimpleErrorResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()), message);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public SimpleErrorResponse resourceNotFound(NotFoundException exception) {
        return new SimpleErrorResponse(HttpStatus.NOT_FOUND.value(), exception.getMessage());
    }

    @ExceptionHandler(SongAlreadyExistRuntimeException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public SimpleErrorResponse resourceConflict(SongAlreadyExistRuntimeException exception) {
        return new SimpleErrorResponse(HttpStatus.CONFLICT.value(), exception.getMessage());
    }

}
