package org.example.service.client;

import org.example.service.dto.StorageDetailsResponse;

import java.util.List;

public interface StorageClient {
    List<StorageDetailsResponse> getStorageDetailsResponses();
}
