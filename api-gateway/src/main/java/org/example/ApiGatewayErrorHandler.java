package org.example;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.gateway.support.ServiceUnavailableException;
import org.springframework.cloud.gateway.support.TimeoutException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

@Component
@Order(-2)
public class ApiGatewayErrorHandler extends AbstractErrorWebExceptionHandler {

    public ApiGatewayErrorHandler(ErrorAttributes errorAttributes,
                                  WebProperties webProperties,
                                  ApplicationContext applicationContext,
                                  ServerCodecConfigurer codecConfigurer) {
        super(errorAttributes, webProperties.getResources(), applicationContext);
        super.setMessageWriters(codecConfigurer.getWriters());
        super.setMessageReaders(codecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::handleError);
    }

    private Mono<ServerResponse> handleError(ServerRequest request) {
        Throwable error = getError(request);

        HttpStatus status;
        String message;

        if (error instanceof NotFoundException ||
                error instanceof NoResourceFoundException) {
            status = HttpStatus.NOT_FOUND;
            message = "The requested route does not exist.";
        } else if (error instanceof ServiceUnavailableException ||
                error instanceof ConnectException) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            message = "Target service is unavailable. Please try again later.";
        } else if (error instanceof TimeoutException) {
            status = HttpStatus.GATEWAY_TIMEOUT;
            message = "Gateway timeout for requested resource";
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "Unexpected error occurred";
        }

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", System.currentTimeMillis());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("path", request.path());
        response.put("message", message);

        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(response));
    }

}
