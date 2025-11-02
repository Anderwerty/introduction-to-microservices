package org.example.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class FileUrl {

    private String fullUrl;

    private String bucketName;

    private String key;

    public static FileUrl copy(FileUrl url, String fullUrl){
        return FileUrl.builder()
                .fullUrl(fullUrl)
                .bucketName(url.getBucketName())
                .key(url.getKey())
                .build();
    }
}
