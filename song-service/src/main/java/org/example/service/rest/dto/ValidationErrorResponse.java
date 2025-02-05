package org.example.service.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
