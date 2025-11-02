package org.example.service.core.impl;

import org.example.service.core.S3Service;
import org.example.service.dto.FileUrl;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class S3ServiceImpl implements S3Service {
    private final S3Client s3Client;

    public S3ServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public FileUrl uploadFile(FileUrl fileUrl, byte[] data) {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(fileUrl.getBucketName())
                        .key(fileUrl.getKey())
                        .build(),
                RequestBody.fromBytes(data));

        URL url = s3Client.utilities()
                .getUrl(b -> b.bucket(fileUrl.getBucketName()).key(fileUrl.getKey()));

        return FileUrl.copy(fileUrl, url.toString());
    }

    @Override
    public byte[] downloadFile(FileUrl fileUrl) {

        ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket(fileUrl.getBucketName())
                        .key(fileUrl.getKey())
                        .build()
        );
        return response.asByteArray();
    }

    @Override
    public void deleteAll(List<FileUrl> fileUrls) {

        Map<String, Set<ObjectIdentifier>> bucketToObjects = fileUrls.stream()
                .collect(Collectors.toMap(FileUrl::getBucketName,
                        fileUrl -> Set.of(ObjectIdentifier.builder().key(fileUrl.getKey()).build()),
                        (a, b) -> {
                            Set<ObjectIdentifier> keys = new HashSet<>(a);
                            keys.addAll(b);
                            return keys;
                        }));

        bucketToObjects.forEach(this::deleteObjectsFromBucket);
    }

    private void deleteObjectsFromBucket(String bucketName, Set<ObjectIdentifier> objectIdentifiers) {
        Delete delete = Delete.builder()
                .objects(objectIdentifiers)
                .build();

        DeleteObjectsRequest request = DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(delete)
                .build();

        s3Client.deleteObjects(request);
    }
}
