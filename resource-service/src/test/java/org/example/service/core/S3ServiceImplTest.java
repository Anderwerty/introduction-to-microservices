package org.example.service.core;

import org.example.service.core.impl.S3ServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
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
}
