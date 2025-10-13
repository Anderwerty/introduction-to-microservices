package org.example.service.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
class ResourceRestServiceClientImplTest {

    @Autowired
    private ResourceRestServiceClientImpl resourceClient;

    @Autowired
    @Qualifier("service.rest.template")
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void getResourceDataShouldReturnBytes() throws Exception {
        Integer resourceId = 1;
        byte[] fakeData = "fake binary data".getBytes();
        String expectedUrl = "http://resource-service/resources/1";

        mockServer.expect(ExpectedCount.once(), requestTo(new URI(expectedUrl)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(fakeData));

        byte[] result = resourceClient.getResourceData(resourceId);

        assertArrayEquals(fakeData, result);
        mockServer.verify();
    }

    @Test
    void getResourceDataShouldNotReturnBytesIfResponseEmpty() throws Exception {
        Integer resourceId = 1;
        String expectedUrl = "http://resource-service/resources/1";

        mockServer.expect(ExpectedCount.once(), requestTo(new URI(expectedUrl)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> resourceClient.getResourceData(resourceId));

        assertEquals(exception.getMessage(), "Failed to get resource data for id: 1");
        mockServer.verify();
    }

    @Test
    void getResourceDataShouldThrowExceptionOnNotFound() throws Exception {
        Integer resourceId = 999;
        String expectedUrl = "http://resource-service/resources/999";

        mockServer.expect(ExpectedCount.once(), requestTo(new URI(expectedUrl)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> resourceClient.getResourceData(resourceId));

        assertEquals(exception.getMessage(), "Failed to get resource data for id: 999");
        mockServer.verify();
    }

}
