package org.example.controller;

import org.example.service.exception.RuntimeIOException;
import org.example.service.rest.dto.ErrorMessage;
import org.example.service.exception.IllegalResourceException;
import org.example.service.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalResourceException.class, IllegalArgumentException.class, RuntimeIOException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage notValidResource(RuntimeException exception) {
        return new ErrorMessage(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage resourceNotFound(ResourceNotFoundException exception) {
        return new ErrorMessage(HttpStatus.NOT_FOUND.value(), exception.getMessage());
    }

}
