package org.example.service.core.impl;

import org.example.service.core.S3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URL;

@Service
public class S3ServiceImpl implements S3Service {
    private final S3Client s3Client;
    private final String bucketName;

    private final String s3EndPoint;

    public S3ServiceImpl(S3Client s3Client,
                         @Value("${aws.s3.bucket-name}") String bucketName,
                         @Value("${aws.s3.endpoint}")String s3EndPoint) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.s3EndPoint = s3EndPoint;
    }

    @Override
    public String uploadFile(String key, byte[] data) {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build(),
                RequestBody.fromBytes(data)
        );

        URL url = s3Client.utilities()
                .getUrl(b -> b.bucket(bucketName).key(key));

        String fileUrl = url.toString();
        return fileUrl;
    }

    @Override
    public byte[] downloadFile(String keyOrUrl) {
        String key = extractKey(keyOrUrl);

        ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build()
        );
        return response.asByteArray();
    }


    private String extractKey(String fileUrl) {
        String prefix = s3EndPoint;
        if (!prefix.endsWith("/")) {
            prefix += "/";
        }
        prefix += bucketName + "/";

        if (fileUrl.startsWith(prefix)) {
            return fileUrl.substring(prefix.length());
        }
        return fileUrl;
    }
}
