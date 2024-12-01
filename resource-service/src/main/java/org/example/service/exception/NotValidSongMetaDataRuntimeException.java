package org.example.service.exception;

import org.example.service.dto.ErrorMessage;

public class NotValidSongMetaDataRuntimeException extends RuntimeException {
    private final ErrorMessage errorMessage;

    public NotValidSongMetaDataRuntimeException(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }
}
