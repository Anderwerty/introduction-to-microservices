package org.example.service.core;

import java.util.List;

public interface ResourceService {
    Integer storeFile(byte[] data);

    byte[] getAudioData(Integer id);

    List<Integer> deleteAll(List<Integer> ids);
}
