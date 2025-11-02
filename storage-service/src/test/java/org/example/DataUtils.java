package org.example;

import lombok.experimental.UtilityClass;
import org.example.entity.Storage;
import org.example.service.dto.StorageCreationRequest;
import org.example.service.dto.StorageDetailsResponse;
import org.example.service.dto.StorageType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@UtilityClass
public class DataUtils {

    public static final Integer ID = 1;

    public static final String BUCKET_NAME = "bucket-name";

    public static final String PATH = "path";

    public static StorageDetailsResponse storageDetailsResponse(StorageType storageType) {
        StorageDetailsResponse storageDetailsResponse = new StorageDetailsResponse();
        storageDetailsResponse.setId(ID);
        storageDetailsResponse.setStorageType(storageType);
        storageDetailsResponse.setBucket(BUCKET_NAME);
        storageDetailsResponse.setPath(PATH);

        return storageDetailsResponse;
    }

    public static StorageCreationRequest storageCreationRequest(StorageType storageType) {
        StorageCreationRequest storageCreationRequest = new StorageCreationRequest();
        storageCreationRequest.setStorageType(storageType);
        storageCreationRequest.setBucket(BUCKET_NAME);
        storageCreationRequest.setPath(PATH);

        return storageCreationRequest;
    }

    public static Storage storage(Integer id, org.example.entity.StorageType storageType) {
        Storage storage = new Storage();

        storage.setId(id);
        storage.setStorageType(storageType);
        storage.setBucket(BUCKET_NAME);
        storage.setPath(PATH);

        return storage;
    }

    public static Storage storage(org.example.entity.StorageType storageType) {
        return storage(null, storageType);
    }

    public static byte[] readFile(String filename) {
        try {
            return Files.readAllBytes(Path.of(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
