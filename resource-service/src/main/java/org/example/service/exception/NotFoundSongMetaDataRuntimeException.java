package org.example.service.exception;

import org.example.service.dto.ErrorMessage;

public class NotFoundSongMetaDataRuntimeException extends RuntimeException {
    private final ErrorMessage errorMessage;

    public NotFoundSongMetaDataRuntimeException(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }
}
