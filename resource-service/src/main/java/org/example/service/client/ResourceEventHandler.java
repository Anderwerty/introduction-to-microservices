package org.example.service.client;

import org.example.service.dto.ResourceEvent;

public interface ResourceEventHandler {
     void handle(ResourceEvent event);
}