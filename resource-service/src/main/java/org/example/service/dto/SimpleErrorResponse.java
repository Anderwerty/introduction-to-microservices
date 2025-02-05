package org.example.service.dto;

import lombok.*;

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
