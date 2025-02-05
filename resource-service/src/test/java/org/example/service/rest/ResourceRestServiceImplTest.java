package org.example.service.rest;

import org.example.service.client.SongClient;
import org.example.service.core.MetadataExtracter;
import org.example.service.core.ResourceService;
import org.example.service.exception.IllegalResourceException;
import org.example.service.dto.Identifiable;
import org.example.service.dto.Identifiables;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.example.DataUtils.FILE_BYTES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceRestServiceImplTest {
    @Mock
    private ResourceService resourceService;

    @Mock
    private MetadataExtracter metadataExtracter;

    @Mock
    private SongClient songClient;

    @InjectMocks
    private ResourceRestServiceImpl resourceRestService;


    @Test
    void storeMetaDataShouldStoreData() {
        when(resourceService.storeFile(FILE_BYTES)).thenReturn(1);
        when(metadataExtracter.getMimeType(FILE_BYTES)).thenReturn("audio/mpeg");

        Identifiable<Integer> identifiable = resourceRestService.storeFile(FILE_BYTES);
        assertEquals(new Identifiable<>(1), identifiable);
    }

    @Test
    void storeMetaDataShouldNotStoreIfNotValidMimeType() {
        when(metadataExtracter.getMimeType(FILE_BYTES)).thenReturn("invalid_content_type");

        IllegalResourceException exception = assertThrows(IllegalResourceException.class,
                () -> resourceRestService.storeFile(FILE_BYTES));
        assertEquals(exception.getDetails(), Map.of("file","Not valid content type invalid_content_type"));
    }

    @Test
    void storeMetaDataShouldNotStoreIfNullArray() {
        IllegalResourceException exception = assertThrows(IllegalResourceException.class,
                () -> resourceRestService.storeFile(null));
        assertEquals(exception.getDetails(), Map.of("file","File doesn't exist"));
    }

    @Test
    void storeMetaDataShouldNotStoreIfArrayIsEmpty() {
        IllegalResourceException exception = assertThrows(IllegalResourceException.class,
                () -> resourceRestService.storeFile(new byte[0]));
        assertEquals(exception.getDetails(), Map.of("file","File is empty"));
    }

    @Test
    void getMetaDataShouldReturnSongMetaData() {
        when(resourceService.getAudioData(1)).thenReturn(FILE_BYTES);

        byte[] fileBytes = resourceRestService.getAudioData("1");
        assertEquals(fileBytes, FILE_BYTES);
    }

    @Test
    void deleteShouldRemoveItems() {
        List<Integer> ids = List.of(1, 2, 3);
        List<Integer> removedItemIds = List.of(1, 2);
        when(resourceService.deleteAll(ids)).thenReturn(removedItemIds);

        Identifiables<Integer> actual = resourceRestService.deleteResources("1,2,3");
        assertEquals(actual, new Identifiables<>(removedItemIds));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void deleteShouldReturnEmptyList(String ids) {
        Identifiables<Integer> actual = resourceRestService.deleteResources(ids);
        assertEquals(actual.getIds().size(), 0);
    }

}