package org.example.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SimpleErrorResponse {
    private String errorCode;
    private String errorMessage;

    public SimpleErrorResponse(int errorCode, String errorMessage){
        this.errorCode = String.valueOf(errorCode);
        this.errorMessage = errorMessage;
    }
}
