package org.example.service.core;

import org.example.service.core.impl.S3ServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ServiceImplTest {
    private static final String BUCKET_NAME = "my-bucket";

    private static final String S3_END_POINT = "http://localhost:4566";

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Utilities s3Utilities;

    private S3ServiceImpl s3Service;

    @BeforeEach
    void setUp() {
        this.s3Service = new S3ServiceImpl(s3Client, BUCKET_NAME, S3_END_POINT);
    }

    @Test
    void uploadFileShouldPutObjectAndReturnUrl() throws MalformedURLException {
        byte[] data = "music 1".getBytes();
        String key = "resource_id";
        URL expectedUrl = new URL("http://localhost:4566/" + BUCKET_NAME + "/" + key);

        when(s3Client.utilities()).thenReturn(s3Utilities);
        when(s3Utilities.getUrl(any(Consumer.class))).thenReturn(expectedUrl);

        String result = s3Service.uploadFile(key, data);

        ArgumentCaptor<PutObjectRequest> putReqCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        ArgumentCaptor<RequestBody> bodyCaptor = ArgumentCaptor.forClass(RequestBody.class);
        verify(s3Client).putObject(putReqCaptor.capture(), bodyCaptor.capture());

        PutObjectRequest actualRequest = putReqCaptor.getValue();
        assertThat(actualRequest.bucket(), is(BUCKET_NAME));
        assertThat(actualRequest.key(), is(key));

        assertThat(result, is(expectedUrl.toString()));
    }

    @Test
    void downloadFileShouldReturnBytesFromS3() {
        byte[] expectedData = "test content".getBytes();
        String key = "resource_id";

        ResponseBytes<GetObjectResponse> mockResponse =
                ResponseBytes.fromByteArray(GetObjectResponse.builder().build(), expectedData);

        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenReturn(mockResponse);

        byte[] result = s3Service.downloadFile(key);

        assertThat(result, is(expectedData));

        ArgumentCaptor<GetObjectRequest> getReqCaptor = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(s3Client).getObjectAsBytes(getReqCaptor.capture());
        assertThat(getReqCaptor.getValue().bucket(), is(BUCKET_NAME));
        assertThat(getReqCaptor.getValue().key(), is(key));
    }

    @Test
    void downloadFileShouldExtractKeyFromHttpUrl() {
        byte[] expectedData = "file data".getBytes();
        String key = "folder/resource_id";
        String fullUrl = "https://my-bucket.s3.amazonaws.com/" + key;

        ResponseBytes<GetObjectResponse> mockResponse =
                ResponseBytes.fromByteArray(GetObjectResponse.builder().build(), expectedData);

        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenReturn(mockResponse);

        byte[] result = s3Service.downloadFile(fullUrl);

        assertThat(result, is(expectedData));

        ArgumentCaptor<GetObjectRequest> getReqCaptor = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(s3Client).getObjectAsBytes(getReqCaptor.capture());
        assertThat(getReqCaptor.getValue().bucket(), is(BUCKET_NAME));
        assertThat(getReqCaptor.getValue().key(), is(fullUrl));
    }

    @Test
    void downloadFileShouldExtractKeyFromS3Url() {
        byte[] expectedData = "file data".getBytes();
        String key = "folder/resource_id";
        String fullUrl = "s3://" + BUCKET_NAME + "/" + key;

        ResponseBytes<GetObjectResponse> mockResponse =
                ResponseBytes.fromByteArray(GetObjectResponse.builder().build(), expectedData);

        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenReturn(mockResponse);

        byte[] result = s3Service.downloadFile(fullUrl);

        assertThat(result, is(expectedData));

        ArgumentCaptor<GetObjectRequest> getReqCaptor = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(s3Client).getObjectAsBytes(getReqCaptor.capture());
        assertThat(getReqCaptor.getValue().bucket(), is(BUCKET_NAME));
        assertThat(getReqCaptor.getValue().key(), is(fullUrl));
    }

    @ParameterizedTest
    @MethodSource("deleteMethodData")
    void deleteAllShouldCallS3WithCorrectKeys(String endPoint) {
        List<String> urlsToDelete = List.of(
                "http://localhost:4566/my-bucket/folder/file1.txt",
                "http://localhost:4566/my-bucket/file2.txt",
                "file3.txt"
        );

        when(s3Client.deleteObjects(any(DeleteObjectsRequest.class)))
                .thenReturn(DeleteObjectsResponse.builder().build());

        S3ServiceImpl s3Service = new S3ServiceImpl(s3Client,BUCKET_NAME, endPoint);
        s3Service.deleteAll(urlsToDelete);

        ArgumentCaptor<DeleteObjectsRequest> captor = ArgumentCaptor.forClass(DeleteObjectsRequest.class);
        verify(s3Client).deleteObjects(captor.capture());

        DeleteObjectsRequest actualRequest = captor.getValue();

        assertThat(actualRequest.bucket(), is(BUCKET_NAME));
        assertThat(
                actualRequest.delete().objects()
                        .stream()
                        .map(ObjectIdentifier::key)
                        .toList(),
                containsInAnyOrder("folder/file1.txt", "file2.txt","file3.txt")
        );
    }


    public static Stream<Arguments> deleteMethodData() {
        return Stream.of(
                Arguments.of(S3_END_POINT),
                Arguments.of(S3_END_POINT+"/")
        );
    }
}
