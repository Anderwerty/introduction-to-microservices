package org.example.service.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.example.service.dto.SimpleErrorResponse;
import org.example.service.dto.ValidationErrorResponse;
import org.example.service.exception.ConflictRuntimeException;
import org.example.service.exception.NotValidSongMetaDataRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Component
@AllArgsConstructor
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper;
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatusCode statusCode = response.getStatusCode();
        return statusCode.is4xxClientError() || statusCode.is5xxServerError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatusCode statusCode = response.getStatusCode();
        if (statusCode.is5xxServerError()) {
            throw new HttpClientErrorException(statusCode);
        }

        if (statusCode.is4xxClientError()) {
            if (statusCode == HttpStatus.CONFLICT) {
                SimpleErrorResponse validationErrorResponse = objectMapper.readValue(response.getBody(), SimpleErrorResponse.class);
                throw new ConflictRuntimeException(validationErrorResponse);
            }
            if (statusCode == HttpStatus.BAD_REQUEST){
                ValidationErrorResponse validationErrorResponse = objectMapper.readValue(response.getBody(), ValidationErrorResponse.class);
                throw new NotValidSongMetaDataRuntimeException(validationErrorResponse);
            }
        }

    }
}
