package org.example.service.core;

import org.example.service.dto.FileUrl;
import org.example.service.core.impl.S3ServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ServiceImplTest {
    private static final String BUCKET_NAME = "my-bucket";

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Utilities s3Utilities;

    @InjectMocks
    private S3ServiceImpl s3Service;

    @Test
    void uploadFileShouldPutObjectAndReturnUrl() throws MalformedURLException {
        byte[] data = "music 1".getBytes();
        String key = "resource_id";
        URL url = new URL("https://localhost:4566/resource_id");

        when(s3Client.utilities()).thenReturn(s3Utilities);
        when(s3Utilities.getUrl(any(Consumer.class))).thenReturn(url);

        FileUrl fileUrl = FileUrl.builder()
                .bucketName(BUCKET_NAME)
                .key(key)
                .build();
        s3Service.uploadFile(fileUrl, data);

        ArgumentCaptor<PutObjectRequest> putReqCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        ArgumentCaptor<RequestBody> bodyCaptor = ArgumentCaptor.forClass(RequestBody.class);
        verify(s3Client).putObject(putReqCaptor.capture(), bodyCaptor.capture());

        PutObjectRequest actualRequest = putReqCaptor.getValue();
        assertAll(
                () -> assertThat(actualRequest.bucket(), is(BUCKET_NAME)),
                () -> assertThat(actualRequest.key(), is(key)));
    }

    @Test
    void downloadFileShouldReturnBytesFromS3() {
        byte[] expectedData = "test content".getBytes();
        String key = "resource_id";

        ResponseBytes<GetObjectResponse> mockResponse =
                ResponseBytes.fromByteArray(GetObjectResponse.builder().build(), expectedData);

        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenReturn(mockResponse);
        FileUrl fileUrl = FileUrl.builder()
                .bucketName(BUCKET_NAME)
                .key(key)
                .build();

        byte[] result = s3Service.downloadFile(fileUrl);

        assertThat(result, is(expectedData));

        ArgumentCaptor<GetObjectRequest> getReqCaptor = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(s3Client).getObjectAsBytes(getReqCaptor.capture());
        assertAll(
                () -> assertThat(getReqCaptor.getValue().bucket(), is(BUCKET_NAME)),
                () -> assertThat(getReqCaptor.getValue().key(), is(key)));
    }

    @Test
    void downloadFileShouldExtractKeyFromHttpUrl() {
        byte[] expectedData = "file data".getBytes();
        String key = "folder/resource_id";

        ResponseBytes<GetObjectResponse> mockResponse = ResponseBytes.fromByteArray(GetObjectResponse.builder().build(),
                expectedData);

        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenReturn(mockResponse);
        FileUrl fileUrl = FileUrl.builder()
                .bucketName(BUCKET_NAME)
                .key(key)
                .build();

        byte[] result = s3Service.downloadFile(fileUrl);

        assertThat(result, is(expectedData));

        ArgumentCaptor<GetObjectRequest> getReqCaptor = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(s3Client).getObjectAsBytes(getReqCaptor.capture());
        assertAll(
                () -> assertThat(getReqCaptor.getValue().bucket(), is(BUCKET_NAME)),
                () -> assertThat(getReqCaptor.getValue().key(), is(key)));
    }

    @Test
    void downloadFileShouldExtractKeyFromS3Url() {
        byte[] expectedData = "file data".getBytes();
        String key = "folder/resource_id";

        ResponseBytes<GetObjectResponse> mockResponse = ResponseBytes.fromByteArray(GetObjectResponse.builder().build(), expectedData);

        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenReturn(mockResponse);
        FileUrl fileUrl = FileUrl.builder()
                .bucketName(BUCKET_NAME)
                .key(key)
                .build();

        byte[] result = s3Service.downloadFile(fileUrl);

        assertThat(result, is(expectedData));

        ArgumentCaptor<GetObjectRequest> getReqCaptor = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(s3Client).getObjectAsBytes(getReqCaptor.capture());
        assertAll(
                () -> assertThat(getReqCaptor.getValue().bucket(), is(BUCKET_NAME)),
                () -> assertThat(getReqCaptor.getValue().key(), is(key)));
    }

    @Test
    void deleteAllShouldCallS3WithCorrectKeys() {
        List<FileUrl> urlsToDelete = Stream.of(
                        "folder/file1.txt",
                        "file2.txt",
                        "file3.txt")
                .map(key -> FileUrl.builder()
                        .key(key)
                        .bucketName(BUCKET_NAME)
                        .build())
                .toList();

        when(s3Client.deleteObjects(any(DeleteObjectsRequest.class)))
                .thenReturn(DeleteObjectsResponse.builder().build());

        s3Service.deleteAll(urlsToDelete);

        ArgumentCaptor<DeleteObjectsRequest> captor = ArgumentCaptor.forClass(DeleteObjectsRequest.class);
        verify(s3Client).deleteObjects(captor.capture());

        DeleteObjectsRequest actualRequest = captor.getValue();

        assertThat(actualRequest.bucket(), is(BUCKET_NAME));
        assertThat(actualRequest.delete().objects().stream().map(ObjectIdentifier::key).toList(),
                containsInAnyOrder("folder/file1.txt", "file2.txt", "file3.txt"));
    }

}
