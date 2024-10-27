package org.example.service.exception;

public class IllegalResourceException extends RuntimeException{

    public IllegalResourceException() {
    }

    public IllegalResourceException(String message) {
        super(message);
    }
}
