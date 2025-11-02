package org.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class FileUrl {
    @Column(name = "full_url")
    private String fullUrl;

    @Column(name = "bucket_name")
    private String bucketName;

    @Column(name = "s3_key")
    private String key;
}
