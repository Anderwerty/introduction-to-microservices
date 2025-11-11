package org.example.service.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.service.dto.ValidationErrorResponse;

@Getter
@RequiredArgsConstructor
public class NotValidSongMetaDataRuntimeException extends RuntimeException {
    private final ValidationErrorResponse validationErrorResponse;

}
