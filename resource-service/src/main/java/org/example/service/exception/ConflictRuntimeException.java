package org.example.service.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.service.dto.SimpleErrorResponse;

@Getter
@RequiredArgsConstructor
public class ConflictRuntimeException extends RuntimeException {

    private final SimpleErrorResponse errorResponse;

}
