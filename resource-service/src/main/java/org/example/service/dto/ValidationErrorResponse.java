package org.example.service.dto;

import lombok.*;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ValidationErrorResponse {
    private Map<String, String> details;
    private String errorCode;
    private String errorMessage;

    public ValidationErrorResponse(String errorCode, Map<String, String> details){
        this.errorCode = errorCode;
        this.errorMessage = "Validation error";
        this.details = details;
    }

    public ValidationErrorResponse(int errorCode, Map<String, String> details){
        this(String.valueOf(errorCode), details);
    }
}
