package org.example.service.client;

import org.example.service.dto.StorageDetailsResponse;
import org.example.service.dto.StorageType;

public interface StorageClient {
    StorageDetailsResponse getStorageByStorageType(StorageType storageType);
}
