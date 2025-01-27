package org.example.service.exception;

public class SongAlreadyExistRuntimeException extends RuntimeException {

    public SongAlreadyExistRuntimeException(String message) {
        super(message);
    }
}
