package org.example.service.core;

public interface S3Service {

    String uploadFile(String key, byte[] data);

    byte[] downloadFile(String key);
}
