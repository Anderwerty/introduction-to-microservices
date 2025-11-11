package org.example.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StorageDetailsResponse {

    private Integer id;
    
    private StorageType storageType;

    private String bucket;

    private String path;
}
