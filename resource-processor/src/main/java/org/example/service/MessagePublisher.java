package org.example.service;

public interface MessagePublisher<M> {

    void publishMessage(M message);
}
