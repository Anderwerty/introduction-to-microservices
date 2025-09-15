package org.example.service.core;

import java.util.List;

public interface S3Service {

    String uploadFile(String key, byte[] data);

    byte[] downloadFile(String key);


    void deleteAll(List<String> keysToDelete);
}
