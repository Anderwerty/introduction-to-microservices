package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "resources")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Embedded
    private FileUrl fileUrl;

    @Column(name = "file_state")
    @Enumerated(EnumType.STRING)
    private FileState fileState;

    public Resource(FileUrl fileUrl) {
        this.fileUrl = fileUrl;
    }
}
