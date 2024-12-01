package org.example.config;

import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationConfig {

    @Bean
    public Mp3Parser mp3Parser (){
        return new Mp3Parser();
    }

    @Bean
    public BodyContentHandler bodyContentHandler(){
        return new BodyContentHandler();
    }

    @Bean
    public RestTemplate restTemplate(ResponseErrorHandler responseErrorHandler){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(responseErrorHandler);

        return restTemplate;
    }
}
