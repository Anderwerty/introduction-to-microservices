package org.example.service;

import org.example.service.client.ResourceServiceClient;
import org.example.service.client.SongServiceClient;
import org.example.service.dto.ResourceEvent;
import org.example.service.dto.SongMetadataDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceEventHandlerImplTest {

    @Mock
    private ResourceServiceClient resourceServiceClient;

    @Mock
    private MetadataExtractor metadataExtractor;

    @Mock
    private SongServiceClient songServiceClient;

    @InjectMocks
    private ResourceEventHandlerImpl handler;

    @Test
    void handleShouldProcessEventSuccessfully() {
        ResourceEvent event = new ResourceEvent(1);
        byte[] resourceData = "fake mp3 data".getBytes();
        SongMetadataDto metadata = createSongMetadata(1, "Song Title", "03:30");

        when(resourceServiceClient.getResourceData(event.getResourceId())).thenReturn(resourceData);
        when(metadataExtractor.extract(resourceData)).thenReturn(metadata);

        handler.handle(event);

        verify(resourceServiceClient).getResourceData(event.getResourceId());
        verify(metadataExtractor).extract(resourceData);
        verify(songServiceClient).saveSongMetadata(metadata);
    }

    @Test
    void handleShouldPropagateExceptionFromResourceService() {
        ResourceEvent event = new ResourceEvent(2);

        when(resourceServiceClient.getResourceData(event.getResourceId()))
                .thenThrow(new RuntimeException("Resource not found"));

        assertThrows(RuntimeException.class, () -> handler.handle(event));

        verify(resourceServiceClient).getResourceData(event.getResourceId());
        verifyNoInteractions(metadataExtractor);
        verifyNoInteractions(songServiceClient);
    }

    @Test
    void handleShouldPropagateExceptionFromMetadataExtractor() {
        ResourceEvent event = new ResourceEvent(3);
        byte[] resourceData = "fake data".getBytes();

        when(resourceServiceClient.getResourceData(event.getResourceId())).thenReturn(resourceData);
        when(metadataExtractor.extract(resourceData)).thenThrow(new RuntimeException("Extraction failed"));

        assertThrows(RuntimeException.class, () -> handler.handle(event));

        verify(resourceServiceClient).getResourceData(event.getResourceId());
        verify(metadataExtractor).extract(resourceData);
        verifyNoInteractions(songServiceClient);
    }

    @Test
    void handleShouldPropagateExceptionFromSongService() {
        ResourceEvent event = new ResourceEvent(4);
        byte[] resourceData = "fake data".getBytes();
        SongMetadataDto metadata = createSongMetadata(4, "Title", "04:00");

        when(resourceServiceClient.getResourceData(event.getResourceId())).thenReturn(resourceData);
        when(metadataExtractor.extract(resourceData)).thenReturn(metadata);
        doThrow(new RuntimeException("Save failed")).when(songServiceClient).saveSongMetadata(metadata);

        assertThrows(RuntimeException.class, () -> handler.handle(event));

        verify(resourceServiceClient).getResourceData(event.getResourceId());
        verify(metadataExtractor).extract(resourceData);
        verify(songServiceClient).saveSongMetadata(metadata);
    }

    private static SongMetadataDto createSongMetadata(int id, String title, String time) {
        SongMetadataDto metadata = new SongMetadataDto();
        metadata.setId(id);
        metadata.setName(title);
        metadata.setArtist("Artist");
        metadata.setAlbum("Album");
        metadata.setDuration(time);
        metadata.setYear("2025");
        return metadata;
    }
}