package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "storages")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Storage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "storage_type")
    @Enumerated(EnumType.STRING)
    private StorageType storageType;

    @Column(name = "bucket")
    private String bucket;

    @Column(name = "path")
    private String path;

}
