package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.gateway.support.ServiceUnavailableException;
import org.springframework.cloud.gateway.support.TimeoutException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.support.DefaultServerCodecConfigurer;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.*;

import reactor.core.publisher.Mono;

import static org.hamcrest.Matchers.containsString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(ApiGatewayErrorHandlerGatewayTest.TestConfig.class)
class ApiGatewayErrorHandlerGatewayTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldHandleSongServiceNotFound() {
        webTestClient.get().uri("/songs/unknown")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody()
                .jsonPath("$.message").value(containsString("The requested route does not exist."));
    }

    @Test
    void shouldHandleResourceServiceUnavailable() {
        webTestClient.get().uri("/resources/down")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
                .expectBody()
                .jsonPath("$.message").value(containsString("unavailable"));
    }

    @Test
    void shouldHandleGatewayTimeout() {
        webTestClient.get().uri("/resources/timeout")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.GATEWAY_TIMEOUT)
                .expectBody()
                .jsonPath("$.message").value(containsString("Gateway timeout for requested resource"));
    }

    @Test
    void shouldHandle500_InternalServerError() {
        webTestClient.get().uri("/songs/error")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody()
                .jsonPath("$.message").value(containsString("Unexpected error occurred"));
    }

    static class TestConfig {

        @Bean
        ErrorAttributes errorAttributes() {
            return new DefaultErrorAttributes();
        }

        @Bean
        WebProperties webProperties() {
            return new WebProperties();
        }

        @Bean
        DefaultServerCodecConfigurer codecConfigurer() {
            return new DefaultServerCodecConfigurer();
        }

        @Bean
        ApiGatewayErrorHandler globalErrorHandler(ErrorAttributes errorAttributes,
                                                  WebProperties webProperties,
                                                  ApplicationContext context,
                                                  DefaultServerCodecConfigurer codecConfigurer) {
            return new ApiGatewayErrorHandler(errorAttributes, webProperties, context, codecConfigurer);
        }

        @Bean
        RouterFunction<ServerResponse> testRoutes() {
            return RouterFunctions
                    .route(RequestPredicates.GET("/songs/unknown"), req ->
                            Mono.error(new NotFoundException("Route missing")))
                    .andRoute(RequestPredicates.GET("/songs/error"), req ->
                            Mono.error(new RuntimeException("Simulated server failure")))
                    .andRoute(RequestPredicates.GET("/resources/down"), req ->
                            Mono.error(new ServiceUnavailableException("Service down")))
                    .andRoute(RequestPredicates.GET("/resources/timeout"), req ->
                            Mono.error(new TimeoutException("Simulated timeout")));
        }
    }
}
