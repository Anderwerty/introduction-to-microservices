package org.example.service.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
@Getter
public class IllegalParameterException extends RuntimeException {
    private final Map<String, String> details;

}
