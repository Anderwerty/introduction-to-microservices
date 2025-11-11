package org.example.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StorageCreationRequest {
    private StorageType storageType;

    private String bucket;

    private String path;
}
