package org.example.service.rest;

import org.example.service.client.MessagePublisher;
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

import static org.example.DataUtils.FILE_BYTES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceRestServiceImplTest {
    @Mock
    private ResourceService resourceService;

    @Mock
    private MetadataExtracter metadataExtracter;

    @Mock
    private MessagePublisher<Identifiable<Integer>> messagePublisher;

    @InjectMocks
    private ResourceRestServiceImpl resourceRestService;


    @Test
    void storeMetaDataShouldStoreData() {
        when(resourceService.storeFile(FILE_BYTES)).thenReturn(1);
        when(metadataExtracter.getMimeType(FILE_BYTES)).thenReturn("audio/mpeg");

        Identifiable<Integer> identifiable = resourceRestService.storeFile(FILE_BYTES);
        assertEquals(new Identifiable<>(1), identifiable);
        verify(messagePublisher).publishMessage(new Identifiable<>(1));
    }

    @Test
    void storeMetaDataShouldNotStoreIfNotValidMimeType() {
        when(metadataExtracter.getMimeType(FILE_BYTES)).thenReturn("invalid_content_type");

        IllegalResourceException exception = assertThrows(IllegalResourceException.class,
                () -> resourceRestService.storeFile(FILE_BYTES));
        assertEquals("Invalid file format: invalid_content_type. Only MP3 files are allowed", exception.getMessage());
    }

    @Test
    void storeMetaDataShouldNotStoreIfNullArray() {
        IllegalResourceException exception = assertThrows(IllegalResourceException.class,
                () -> resourceRestService.storeFile(null));
        assertEquals("File doesn't exist", exception.getMessage());
    }

    @Test
    void storeMetaDataShouldNotStoreIfArrayIsEmpty() {
        IllegalResourceException exception = assertThrows(IllegalResourceException.class,
                () -> resourceRestService.storeFile(new byte[0]));
        assertEquals("File is empty", exception.getMessage());
    }

    @Test
    void getMetaDataShouldReturnSongMetaData() {
        when(resourceService.getAudioData(1)).thenReturn(FILE_BYTES);

        byte[] fileBytes = resourceRestService.getAudioData("1");
        assertEquals(FILE_BYTES, fileBytes);
    }

    @Test
    void deleteShouldRemoveItems() {
        List<Integer> ids = List.of(1, 2, 3);
        List<Integer> removedItemIds = List.of(1, 2);
        when(resourceService.deleteAll(ids)).thenReturn(removedItemIds);

        Identifiables<Integer> actual = resourceRestService.deleteResources("1,2,3");
        assertEquals(new Identifiables<>(removedItemIds), actual);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void deleteShouldReturnEmptyList(String ids) {
        Identifiables<Integer> actual = resourceRestService.deleteResources(ids);
        assertEquals(0, actual.getIds().size());
    }

}