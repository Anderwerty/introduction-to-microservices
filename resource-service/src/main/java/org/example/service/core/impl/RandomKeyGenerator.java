package org.example.service.core.impl;

import org.example.service.core.KeyGenerator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RandomKeyGenerator implements KeyGenerator {
    @Override
    public String generateKey() {
        return UUID.randomUUID().toString();
    }
}
