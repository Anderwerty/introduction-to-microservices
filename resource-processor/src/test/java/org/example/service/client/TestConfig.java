package org.example.service.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.ApplicationConfig;
import org.springframework.context.annotation.*;

@Configuration
@PropertySource("application.properties")
@ComponentScan("org.example.service.client")
@Import({ApplicationConfig.class})
public class TestConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
