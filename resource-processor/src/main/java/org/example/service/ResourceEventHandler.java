package org.example.service;

import org.example.service.dto.ResourceEvent;

public interface ResourceEventHandler {
     void handle(ResourceEvent event);
}
