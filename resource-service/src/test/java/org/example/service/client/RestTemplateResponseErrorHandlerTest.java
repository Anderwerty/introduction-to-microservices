package org.example.service.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestTemplateResponseErrorHandlerTest {

    @InjectMocks
    private RestTemplateResponseErrorHandler errorHandler;
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();


    @Test
    void testHasErrorClientError() throws IOException {
        ClientHttpResponse response = mock(ClientHttpResponse.class);
        when(response.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);

        assertTrue(errorHandler.hasError(response));
    }

    @Test
    void testHasErrorServerError() throws IOException {
        ClientHttpResponse response = mock(ClientHttpResponse.class);
        when(response.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);

        assertTrue(errorHandler.hasError(response));
    }

    @Test
    void testHasErrorNoError() throws IOException {
        ClientHttpResponse response = mock(ClientHttpResponse.class);
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);

        assertFalse(errorHandler.hasError(response));
    }

    @Test
    void testHandleErrorServerErrorThrowsException() throws IOException {
        ClientHttpResponse response = mock(ClientHttpResponse.class);
        when(response.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> errorHandler.handleError(response));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
    }

    @Test
    void testHandleErrorClientErrorDoesNothing() throws IOException {
        ClientHttpResponse response = mock(ClientHttpResponse.class);
        when(response.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);

        assertDoesNotThrow(() -> errorHandler.handleError(response));
    }
}
