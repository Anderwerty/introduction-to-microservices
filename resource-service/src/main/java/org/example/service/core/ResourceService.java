package org.example.service.core;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResourceService {
    Integer storeFile(MultipartFile file);

    byte[] getAudioData(Integer id);

    List<Integer> deleteAll(List<Integer> ids);
}
