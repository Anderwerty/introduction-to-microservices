package org.example;

import org.example.service.MetadataExtracter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootApplication
public class ResourceProcessorApplication implements CommandLineRunner {

    @Autowired
    private  MetadataExtracter extractor;

    public static void main(String[] args) {
        SpringApplication.run(ResourceProcessorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {


        byte[] mp3File = readFile("fortecya-bahmut.mp3");
        var metadata = extractor.extract(mp3File);
        System.out.println("Extracted metadata: " + metadata);
    }

    private byte[] readFile(String resourceName) {
        try (var inputStream = new ClassPathResource(resourceName).getInputStream()) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Cannot read resource: " + resourceName, e);
        }
    }
}
