package org.example.controller;

import org.example.service.exception.NotFoundException;
import org.example.service.rest.dto.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage notValidResource(RuntimeException exception) {
        return new ErrorMessage(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage resourceNotFound(NotFoundException exception) {
        return new ErrorMessage(HttpStatus.NOT_FOUND.value(), exception.getMessage());
    }


}
