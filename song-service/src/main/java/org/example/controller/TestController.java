package org.example.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
public class TestController {
    private final String message;

    public TestController(@Value("${song-service.test.message}") String message) {
        this.message = message;
    }

    @GetMapping("/songs/test/test")
    public String getMessage() {
        return message;
    }
}
