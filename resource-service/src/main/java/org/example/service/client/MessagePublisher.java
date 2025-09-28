package org.example.service.client;

public interface MessagePublisher<M> {

    void publishMessage(M message);
}
