package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "resources")
@Data
public class Resource {

    @Id
    private Integer id;

    @Column(name = "file")
    @Lob
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private byte[] file;


    public Resource(byte[] file) {
        this.file = file;
    }
}
