package org.example.service.client;

import org.example.entity.FileState;
import org.example.entity.FileUrl;
import org.example.entity.Resource;
import org.example.repository.ResourceRepository;
import org.example.service.core.S3Service;
import org.example.service.dto.ResourceEvent;
import org.example.service.dto.StorageDetailsResponse;
import org.example.service.dto.StorageType;
import org.example.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourcePermanentEventHandlerImplTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private StorageClient storageClient;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private ResourcePermanentEventHandlerImpl eventHandler;

    @Test
    void handleShouldMoveFileToPermanentAndSaveResource() {
        Integer resourceId = 1;
        ResourceEvent event = new ResourceEvent();
        event.setResourceId(resourceId);

        FileUrl fileUrl = new FileUrl("https://staging-bucket/staging/path/file.txt", "staging-bucket", "staging/path/file.txt");
        Resource resource = new Resource();
        resource.setFileUrl(fileUrl);
        resource.setFileState(FileState.STAGING);

        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));

        StorageDetailsResponse stagingStorage = StorageDetailsResponse.builder()
                .storageType(StorageType.STAGING)
                .bucket("staging-bucket")
                .path("staging/path/")
                .build();
        StorageDetailsResponse permanentStorage = StorageDetailsResponse.builder()
                .storageType(StorageType.PERMANENT)
                .bucket("permanent-bucket")
                .path("permanent/path/")
                .build();

        when(storageClient.getStorageDetailsResponses()).thenReturn(List.of(stagingStorage,permanentStorage));

        byte[] fileBytes = "file-content".getBytes();
        when(s3Service.downloadFile(any())).thenReturn(fileBytes);

        org.example.service.dto.FileUrl uploadedFileUrl = org.example.service.dto.FileUrl.builder()
                .bucketName("permanent-bucket")
                .key("permanent/path/file.txt")
                .fullUrl("https://permanent-bucket/permanent/path/file.txt")
                .build();
        when(s3Service.uploadFile(any(), eq(fileBytes))).thenReturn(uploadedFileUrl);

        eventHandler.handle(event);

        ArgumentCaptor<Resource> resourceCaptor = ArgumentCaptor.forClass(Resource.class);
        verify(resourceRepository).save(resourceCaptor.capture());

        Resource savedResource = resourceCaptor.getValue();

        assertEquals(FileState.PERMANENT, savedResource.getFileState());
        assertEquals("permanent-bucket", savedResource.getFileUrl().getBucketName());
        assertEquals("permanent/path/file.txt", savedResource.getFileUrl().getKey());
    }

    @Test
    void handleShouldThrowExceptionWhenResourceNotFound() {
        Integer resourceId = 99;
        ResourceEvent event = new ResourceEvent();
        event.setResourceId(resourceId);

        when(resourceRepository.findById(resourceId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventHandler.handle(event));
    }

}
