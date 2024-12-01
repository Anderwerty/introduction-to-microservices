package org.example.service.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage {
    private int status;
    private String description;
    private Map<String, String> details;

    public ErrorMessage(int status, String description) {
        this.status = status;
        this.description = description;
    }
}
