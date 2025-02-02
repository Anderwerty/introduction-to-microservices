package org.example.service.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.service.dto.ErrorMessage;

@Getter
@RequiredArgsConstructor
public class ConflictRuntimeException extends RuntimeException {

    private final ErrorMessage errorMessage;

}
