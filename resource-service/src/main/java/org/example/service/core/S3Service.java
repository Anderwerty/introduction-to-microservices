package org.example.service.core;

import org.example.service.dto.FileUrl;

import java.util.List;

public interface S3Service {

    FileUrl uploadFile(FileUrl fileUrl, byte[] data);

    byte[] downloadFile(FileUrl fileUrl);


    void deleteAll(List<FileUrl> fileUrls);
}
